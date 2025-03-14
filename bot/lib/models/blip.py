import os
import torch
import numpy as np

from transformers import BlipProcessor, BlipForConditionalGeneration

from lib import utils


class BLIP:
  
  def __init__(self, model_name):
    self.processor = None
    self.model = None
    self.device = "cuda" if torch.cuda.is_available() else "cpu"
    
    model_dir = os.getenv("MODEL_CACHE_DIR")
    model_dir = f"{model_dir}/{model_name}"
    if not os.path.exists(model_dir):
      os.makedirs(model_dir)
    
    if os.listdir(model_dir):
      self.processor = BlipProcessor.from_pretrained(model_dir)
      self.model = BlipForConditionalGeneration.from_pretrained(model_dir).to(self.device)
      
    else:
      self.processor = BlipProcessor.from_pretrained(model_name)
      self.model = BlipForConditionalGeneration.from_pretrained(model_name).to(self.device)
      self.processor.save_pretrained(model_dir)
      self.model.save_pretrained(model_dir)
      
  def encode(self, texts=None, images=None):
    if not texts and not images:
      return np.ndarray(), []
    
    return self._encode(texts=texts, images=images)
  
  def _encode(self, texts=None, images=None):
    
    if texts:
      inputs = self.processor(text=texts, return_tensors="pt", padding=True)
    elif images:
      inputs = self.processor(images=images, return_tensors="pt", padding=True)
    inputs = {k: v.to(self.device) for k, v in inputs.items()}
    
    print(inputs)
    with torch.no_grad():
      if not images:
        outputs = self.model.text_decoder(**inputs).cpu.numpy()
      else:
        outputs = self.model.generate(**inputs).cpu().numpy()
    
    print(outputs)
    print(self.processor.decode(outputs[0], skip_special_tokens=True))
    return outputs