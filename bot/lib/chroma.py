import os
import numpy
from PIL import Image

import torch
import chromadb

from lib import utils
from .models import clip, blip

class VectorModel:
  _models = {}
  
  def __new__ (cls, model_name, model_type):
    if model_name not in cls._models:
      cls._models[model_name] = super(VectorModel, cls).__new__(cls)
    return cls._models[model_name]
  
  def __init__(self, model_name, model_type):
    self.model_name = model_name
    if model_type == "clip":
      self.model = clip.CLIP(model_name)
    elif model_type == "blip":
      self.model = blip.BLIP(model_name)
    else:
      raise ValueError("Invalid model type.")
  
  def encode(self, *args, **kwargs):
    return self.model.encode(*args, **kwargs)
  
  
class VectorStore:
  _client = None
  
  def __init__(self, model_name, model_type, collection_name):
    self.model_name = model_name
    self.collection_name = collection_name
    self.model = VectorModel(model_name, model_type)
    
    if not VectorStore._client:
      VectorStore._client = chromadb.PersistentClient(path=os.getenv("CHROMA_PATH"))
    self.client = VectorStore._client
    
    self.collection = self.client.create_collection(collection_name, get_or_create=True)
  
  def store(self, objs=[], metas=[]):
    if not objs:
      return []
    if len(objs) != len(metas):
      raise ValueError("Length of objs and metas must be the same.")
    
    embeds, docs, metadatas = self.model.encode(objs=objs, metas=metas)
    
    if not embeds.all():
      return []
    
    self.collection.add(
      documents=docs,
      embeddings=embeds,
      metadatas=metadatas,
      ids=[meta["id"] for meta in metadatas]
    )
    
    return docs
  
  def search(self, query, n=1):
    if not query:
      return []
    
    query_embed, _, _ = self.model.encode(objs=[query], flag=True)
    results = self.collection.query(query_embeddings=query_embed, 
                                    n_results=n)
    return results if results else []