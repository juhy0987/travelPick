import os
import numpy
from PIL import Image

import torch
import chromadb
from transformers import AutoProcessor, AutoModel

from lib import utils

class VectorModel:
  _models = {}
  
  def __new__ (cls, model_name):
    if model_name not in cls._models:
      cls._models[model_name] = super(VectorModel, cls).__new__(cls)
    return cls._models[model_name]
  
  def __init__(self, model_name):
    self.model_name = model_name
    self.processor = None
    self.model = None
    self.device = "cuda" if torch.cuda.is_available() else "cpu"
    
    model_dir = os.getenv("MODEL_CACHE_DIR")
    model_dir = f"{model_dir}/{model_name}"
    if not os.path.exists(model_dir):
      os.makedirs(model_dir)
    
    if os.listdir(model_dir):
      self.processor = AutoProcessor.from_pretrained(model_dir)
      self.model = AutoModel.from_pretrained(model_dir).to(self.device)
      
    else:
      self.processor = AutoProcessor.from_pretrained(model_name)
      self.model = AutoModel.from_pretrained(model_name).to(self.device)
      self.processor.save_pretrained(model_dir)
      self.model.save_pretrained(model_dir)

  def encode(self, objs):
    if not objs:
      return []
    
    if isinstance(objs[0], str):
      return self._encode_text(objs)
    elif isinstance(objs[0], Image.Image):
      return self._encode_image(objs)
    
    raise ValueError("Unsupported type.")
  
  def _encode_text(self, texts):
    if not texts:
      return []
    elif not isinstance(texts[0], str):
      raise ValueError("Input must be a string.")
    
    inputs = self.processor(text=texts, return_tensors="pt", padding=True)
    inputs = {k: v.to(self.device) for k, v in inputs.items()}
    with torch.no_grad():
      outputs = self.model.get_text_features(**inputs)
    return outputs.cpu().numpy()

  def _encode_image(self, images):
    if not images:
      return []
    elif not isinstance(images[0], Image.Image):
      raise ValueError("Input must be an image.")
    
    inputs = self.processor(images=images, return_tensors="pt", padding=True)
    inputs = {k: v.to(self.device) for k, v in inputs.items()}
    with torch.no_grad():
      outputs = self.model.get_image_features(**inputs)
    return outputs.cpu().numpy()
  
  
class VectorStore:
  _client = None
  
  def __init__(self, model_name, collection_name):
    self.model_name = model_name
    self.collection_name = collection_name
    self.model = VectorModel(model_name)
    
    if not VectorStore._client:
      VectorStore._client = chromadb.PersistentClient(path=os.getenv("CHROMA_PATH"))
    self.client = VectorStore._client
    
    self.collection = self.client.create_collection(collection_name, get_or_create=True)
  
  def store(self, objs=[], metas=[]):
    if not objs:
      return []
    if len(objs) != len(metas):
      raise ValueError("Length of objs and metas must be the same.")
    
    texts, text_metas, images, image_metas = [], [], [], []
    for obj, meta in zip(objs, metas):
      if isinstance(obj, str):
        texts.append(obj)
        text_metas.append(meta)
      elif isinstance(obj, Image.Image):
        images.append(obj)
        image_metas.append(meta)
    
    text_embeds = self.model.encode(texts)
    image_embeds = self.model.encode(images)
    
    self.collection.add(
      documents=texts+[utils.image_to_base64(image) for image in images],
      embeddings=numpy.concatenate((text_embeds, image_embeds), axis=0),
      metadatas=text_metas+image_metas,
      ids=[meta["id"] for meta in metas]
    )
    
    return texts, images
  
  def search(self, query, n=1):
    if not query:
      return []
    
    query_embed = self.model.encode([query])
    results = self.collection.query(query_embeddings=query_embed, 
                                    n_results=n)
    return results if results else []