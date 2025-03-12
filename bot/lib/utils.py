import io
import base64

def image_to_base64(image):
  img_byte_arr = io.BytesIO()
  image.save(img_byte_arr, format="JPEG")
  return base64.b64encode(img_byte_arr.getvalue()).decode("utf-8")