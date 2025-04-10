import io
import numpy as np
import base64
from PIL import Image

def image_to_base64(image: Image.Image, format="JPEG"):
  img_byte_arr = io.BytesIO()
  image.save(img_byte_arr, format=format)
  return base64.b64encode(img_byte_arr.getvalue()).decode("utf-8")

def base64_to_image(base64_string, format="JPEG"):
  return Image.open(io.BytesIO(base64.b64decode(base64_string)), formats=[format])

def image_to_bytes(image: Image.Image, format="jpeg"):
  
  img_byte_arr = io.BytesIO()
  image.save(img_byte_arr, format=format)
  img_byte_arr = img_byte_arr.getvalue()
  return img_byte_arr

def parse_dataurl(dataurl):
  try:
    dataurl = dataurl.split("/", 1)[1]
    format, dataurl = dataurl.split(";", 1)
    return base64_to_image(dataurl.split(",")[1], format)
  except Exception as e:
    print(e)
    return None

def normalize_vector(vector):
  norm = np.linalg.norm(vector)
  return vector / norm if norm != 0 else vector