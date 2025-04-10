import os
import numpy
from PIL import Image

import torch
import chromadb
from chromadb.api.configuration import CollectionConfigurationInternal, ConfigurationParameter

from lib import utils
from .models import clip, blip, gemma, longformer

class VectorModel:
  _models = {}
  
  def __new__ (cls, model_name, model_type):
    if model_name not in cls._models:
      cls._models[model_name] = super(VectorModel, cls).__new__(cls)
    return cls._models[model_name]
  
  def __init__(self, model_name, model_type):
    self.model_name = model_name
    match model_type:
      case "clip":
        self.model = clip.CLIP(model_name)
      case "blip":
        self.model = blip.BLIP(model_name)
      case "gemma":
        self.model = gemma.GEMMA(model_name)
      case "longformer":
        self.model = longformer.Longformer(model_name)
      case _:
        raise ValueError("Invalid model type.")
  
  def encode(self, *args, **kwargs):
    embeds, docs = self.model.encode(*args, **kwargs)
    return [utils.normalize_vector(embed) for embed in embeds], docs
  
  
class VectorStore:
  _client = None
  
  def __init__(self, model_name, model_type, collection_name):
    self.model_name = model_name
    self.collection_name = collection_name
    if model_name and model_type:
      self.model = VectorModel(model_name, model_type)
    else:
      self.model = None
    
    if not VectorStore._client:
      VectorStore._client = chromadb.PersistentClient(path=os.getenv("CHROMA_PATH"))
    self.client = VectorStore._client
    
    self.collection = self.client.create_collection(
      collection_name, 
      metadata={"hnsw:space": "cosine"}, 
      get_or_create=True
    )
  
  # def store(self, objs=[], metas=[], func=None):
  def store(self, texts=None, images=None, metas=[], func=None):
    # if not objs:
    #   return [], []
    # if len(objs) != len(metas):
    #   raise ValueError("Length of objs and metas must be the same.")
    
    # texts = None
    # images = None
    
    # if isinstance(objs[0], str):
    #   texts = objs
    # elif isinstance(objs[0], Image.Image):
    #   images = objs
    # else:
    #   raise ValueError("Invalid object type.")
      
    embeds, docs = self.model.encode(texts=texts, images=images)
    print(embeds)
    metadatas = metas
    
    self.collection.add(
      documents=docs,
      embeddings=embeds,
      metadatas=metadatas,
      ids=[meta["id"] for meta in metadatas]
    )
    
    return embeds, docs
  
  def search(self, query, n=1, query_type="text"):
    if not query:
      return {"ids": [], "metadatas": [], "documents": []}
    
    match query_type:
      case "text":
        query_embed, _, = self.model.encode(texts=[query])
      case "image":
        query_embed, _, = self.model.encode(images=[query])
    results = self.collection.query(query_embeddings=query_embed, 
                                    n_results=n)
    return results if results else {"ids": [], "metadatas": [], "documents": []}

  def add_cluster_embed(self, ids, embeddings, metas, docs):
    targets = self.collection.get(ids=ids, include=["embeddings", "metadatas", "documents"])
    
    selected = []
    not_selected = {"ids": [], "embeddings": [], "metadatas": [], "documents": []}
    for src, src_embed, meta, doc in zip(ids, embeddings, metas, docs):
      if src not in targets["ids"]:
        not_selected["ids"].append(src)
        not_selected["embeddings"].append(src_embed)
        not_selected["metadatas"].append(meta)
        not_selected["documents"].append(doc)
      else:
        selected.append(src_embed)

    targets["embeddings"] = selected
    for target, target_embed, target_meta, target_doc, src_embed \
      in zip(targets["ids"], targets["embeddings"], targets["metadatas"], targets["documents"], embeddings):
      if target_meta.get("count") is None:
        target_meta["count"] = 0
      target_embed = (target_embed * target_meta["count"] + src_embed) / (target_meta["count"] + 1)
      target_meta["count"] += 1
      
      self.collection.update(
        ids=[target],
        embeddings=[target_embed],
        metadatas=[target_meta],
        documents=[target_doc]
      )
    
    self.add(**not_selected)
    
    return not_selected
  
  def add(self, *args, **kwargs):
    return self.collection.add(*args, **kwargs)

  def get(self, *args, **kwargs):
    return self.collection.get(*args, **kwargs)
  
  def delete(self, ids=[]):
    return self.collection.delete(where={"parent": {"$in": ids}})

# get, peek, add, query, modify, update, upsert, delete