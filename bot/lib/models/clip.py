import os
import torch
import numpy as np
from PIL import Image

from transformers import CLIPProcessor, CLIPModel

from lib import utils

class CLIP:
  def __init__(self, model_name):
    self.model_name = model_name
    self.processor = None
    self.model = None
    self.device = "cuda" if torch.cuda.is_available() else "cpu"
    
    self.load()
  
  def load(self):
    model_dir = os.getenv("MODEL_CACHE_DIR")
    model_dir = f"{model_dir}/{self.model_name}"
    if not os.path.exists(model_dir):
      os.makedirs(model_dir)
    
    if os.listdir(model_dir):
      self.processor = CLIPProcessor.from_pretrained(model_dir)
      self.model = CLIPModel.from_pretrained(model_dir).to(self.device)
      
    else:
      self.processor = CLIPProcessor.from_pretrained(self.model_name)
      self.model = CLIPModel.from_pretrained(self.model_name).to(self.device)
      self.processor.save_pretrained(model_dir)
      self.model.save_pretrained(model_dir)
  
  def encode(self, texts=None, images=None):
    if not texts and not images:
      return np.ndarray(), []
    
    return self._encode(texts=texts, images=images)
  
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
        results = [(self.model.get_image_features(
          pixel_values=inputs["pixel_values"]
        ).cpu().numpy())]
    
    return results[0], [utils.image_to_base64(img) for img in images] if images else texts
