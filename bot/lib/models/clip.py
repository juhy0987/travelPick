import os
import torch
import numpy as np
from PIL import Image

from transformers import CLIPProcessor, CLIPModel

from lib import utils

class CLIP:
  def __init__(self, model_name):
    self.processor = None
    self.model = None
    self.device = "cuda" if torch.cuda.is_available() else "cpu"
    
    model_dir = os.getenv("MODEL_CACHE_DIR")
    model_dir = f"{model_dir}/{model_name}"
    if not os.path.exists(model_dir):
      os.makedirs(model_dir)
    
    if os.listdir(model_dir):
      self.processor = CLIPProcessor.from_pretrained(model_dir)
      self.model = CLIPModel.from_pretrained(model_dir).to(self.device)
      
    else:
      self.processor = CLIPProcessor.from_pretrained(model_name)
      self.model = CLIPModel.from_pretrained(model_name).to(self.device)
      self.processor.save_pretrained(model_dir)
      self.model.save_pretrained(model_dir)
  
  def encode(self, *args, **kwargs):
    objs = kwargs.get("objs", [])
    metas = kwargs.get("metas", [])
    
    if not objs:
      return []
    
    if kwargs.get("flag") == True:
      return self._encode(texts=objs), [], []
    
    texts, text_metas, images, image_metas = [], [], [], []
    for obj, meta in zip(objs, metas):
      if isinstance(obj, str):
        meta["source"] = "text"
        texts.append(obj)
        text_metas.append(meta)
      elif isinstance(obj, Image.Image):
        meta["source"] = "image"
        images.append(obj)
        image_metas.append(meta)
    
    if texts and images:
      return (
        self._encode(texts=texts, images=images), 
        texts+[utils.image_to_base64(img) for img in images], 
        text_metas+image_metas
      )
    elif texts:
      return (
        self._encode(texts=texts), 
        texts, 
        text_metas
      )
    elif images:
      return (
        self._encode(images=images), 
        [utils.image_to_base64(img) for img in images], 
        image_metas
      )
    else:
      return [], [], []
  
  def _encode(self, texts=None, images=None):
    
    inputs = self.processor(images=images, text=texts, return_tensors="pt", padding=True)
    inputs = {k: v.to(self.device) for k, v in inputs.items()}
    
    results = []
    with torch.no_grad():
      if inputs.get("input_ids") is not None \
        and inputs.get("attention_mask") is not None:
        results.append(self.model.get_text_features(
          input_ids=inputs["input_ids"],
          attention_mask=inputs["attention_mask"]
        ).cpu().numpy())
      
      if inputs.get("pixel_values") is not None:
        results.append(self.model.get_image_features(
          pixel_values=inputs["pixel_values"]
        ).cpu().numpy())
    
    return np.concatenate([result for result in results if result is not None], axis=0)
