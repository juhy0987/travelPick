import os
import torch
import numpy as np

from transformers import LongformerTokenizer, LongformerModel

class Longformer:
  def __init__(self, model_name):
    self.model_name = model_name
    self.model = None
    # self.device = "cuda" if torch.cuda.is_available() else "cpu"
    self.device = "cpu"
    
    self.load()
  
  def load(self):
    model_dir = os.getenv("MODEL_CACHE_DIR")
    model_dir = f"{model_dir}/{self.model_name}"
    if not os.path.exists(model_dir):
      os.makedirs(model_dir)
    
    if os.listdir(model_dir):
      self.tokenizer = LongformerTokenizer.from_pretrained(model_dir)
      self.model = LongformerModel.from_pretrained(model_dir).to(self.device)
    else:
      self.tokenizer = LongformerTokenizer.from_pretrained(self.model_name)
      self.model = LongformerModel.from_pretrained(self.model_name).to(self.device)
      self.tokenizer.save_pretrained(model_dir)
      self.model.save_pretrained(model_dir)
    
  def encode(self, texts=None, images=None):
    if not texts:
      return np.ndarray(), []
    if images:
      raise ValueError("Longformer does not support image inputs.")

    inputs = self.tokenizer(texts, return_tensors="pt", padding=True, truncation=True,
                            max_length=4096)
    inputs = inputs.to(self.device)
    
    outputs = self.model(**inputs)[0].cpu().detach().numpy()
    outputs = [np.mean(vec, axis=0).tolist() for vec in outputs]
    return outputs, texts
    