import io
import os
import time
import requests
import json
from dotenv import load_dotenv
from shapely.geometry import Point
from geoalchemy2.shape import from_shape
from PIL import Image

from db import crud, model, database

from lib import chroma

api_cnt = 0
def get_location(location_id, language="ko"):
  API_KEY = os.getenv("API_KEY")
  
  url = f"https://api.content.tripadvisor.com/api/v1/location/{location_id}/details?language={language}&currency=USD&key={API_KEY}"
  headers = {"accept": "application/json"}
  
  try:
    time.sleep(0.1)
    response = requests.get(url, headers=headers)
    api_cnt += 1
    return response.text
  except Exception as e:
    return ""

def search_location(query, category="geos", language="ko"):
  API_KEY = os.getenv("API_KEY")
  global api_cnt
  
  url = f"https://api.content.tripadvisor.com/api/v1/location/search?key={API_KEY}&searchQuery={query}&category={category}&language={language}"
  headers = {"accept": "application/json"}
  
  try:
    time.sleep(0.1)
    response = requests.get(url, headers=headers)
    api_cnt += 1
    return response.text
  except Exception as e:
    # raise e
    return ""

def get_image(location_id, offset=0, limit=None, language="ko"):
  API_KEY = os.getenv("API_KEY")
  global api_cnt
  
  url = f"https://api.content.tripadvisor.com/api/v1/location/{location_id}/photos?language={language}&currency=USD&key={API_KEY}&offset={offset}"
  url += f"&limit={limit}" if limit else ""
  headers = {"accept": "application/json"}
  
  try:
    time.sleep(0.1)
    response = requests.get(url, headers=headers)
    api_cnt += 1
    return response.text
  except Exception as e:
    print(str(e))
    return ""

def parse_json(data):
  data = json.loads(data)
  if "data" in data:
    data = data["data"]
    if "result" in data:
      data = data["result"]
  
  if data:
    return data
  else:
    return None

def load_image(location_id, offset=0, limit=None):
  data = parse_json(get_image(location_id, offset, limit))
  if not data:
    return [], 0
  
  next_offset = offset+len(data)
  images = []
  for image in data:
    if "images" in image:
      try:
        time.sleep(0.1)
        image_data = io.BytesIO(requests.get(image["images"]["large"]["url"]).content)
        images.append(Image.open(image_data))
      except Exception as e:
        print(str(e))
        pass
  
  return images, next_offset

def file_read(file_path):
  with open(file_path, "r") as f:
    while (tmp := f.readline()):
      yield tmp
  yield None
    
def get_division():
  division = ["country", "state", "city", "street2", "street1"]
  division = {d: i for i, d in enumerate(division)}

  while True:
    target = yield
    if target in division:
      yield division[target]
    else:
      yield -1

def main():
  text_model_type="longformer"
  text_collection_name = "text"
  image_model_type="clip"
  image_collection_name = "image"
  file_path = "../output.txt"
  
  text_store = chroma.VectorStore(os.getenv("LONGFORMER"), text_model_type, text_collection_name)
  image_store = chroma.VectorStore(os.getenv("CLIP"), image_model_type, image_collection_name)
  
  cache = {}
  
  divider = get_division()
  reader = file_read(file_path)
  next(divider)
  db = next(database.getDB())
  
  cnt = 0
  while (line := next(reader)):
    data = parse_json(search_location(line.strip()))
    
    if not data:
      continue
    
    for location in data:
      tracer = crud.trace_route(db)
      next(tracer)
      
      address = location["address_obj"]
      address_obj = []
      
      for division, area in address.items():
        division = divider.send(division)
        next(divider)
        if division < 0 or not area:
          continue
        
        address_obj.append((division, area))
      address_obj.sort()
    
      flag = False
      
      for i, v in enumerate(address_obj):
        _, area = v
        if not tracer.send(area):
          flag = True
          break
        next(tracer)
      if flag:
        tracer.close()
        continue
      
      parent = tracer.send(None)

      if crud.search_location(db, model.Location(
        name=location["name"],
        parent_id=parent
      )):
        tracer.close()
        continue
        
      _location = parse_json(get_location(location["location_id"]))
      location = crud.create_location(db, model.Location(
        parent_id=parent,
        name=_location["name"],
        alias=location["location_id"],
        coordinates=from_shape(
          Point(_location['latitude'], _location['longitude']), 
          srid=4326
        ),
        timezone=_location["timezone"]
      ))
      # print(_location)
      resort = crud.create_resort(db, model.Resort(
        location_id=location.id,
        name=_location["name"],
        alias=_location["location_id"],
        description=(_location["description"] if "description" in _location else ""),
      ))
      
      text_store.store(
        texts=[_location["name"]],
        metas=[{"id": resort.id, 
                "name": resort.name,
                "type": "name", 
                "parent": resort.id, 
                "index": "name",
                "count": 1
        }]
      )
      
      if "description" in _location:
        text_store.store(
          texts=[_location["description"]],
          metas=[{"id": resort.id+".description", 
                  "name": resort.name,
                  "type": "description", 
                  "parent": resort.id, 
                  "index": "name",
                  "count": 1
          }]
        )
      
      print(f"location: {location.name} -> resort: {resort}")
      cnt += 1
      
      if cnt % 1000 == 0:
        print(f"cnt: {cnt}")
        return

if __name__ == "__main__":
  load_dotenv("../../.env")
  
  main()