import os
import time
import requests
import json
from dotenv import load_dotenv
from shapely.geometry import Point
from geoalchemy2.shape import from_shape

from db.crud import *

from lib import chroma


def get_location(location_id, language="ko"):
  API_KEY = os.getenv("API_KEY")
  
  url = f"https://api.content.tripadvisor.com/api/v1/location/{location_id}/details?language={language}&currency=USD&key={API_KEY}"
  headers = {"accept": "application/json"}
  
  try:
    time.sleep(0.1)
    response = requests.get(url, headers=headers)
    return response.text
  except Exception as e:
    return ""

def search_location(query, category="geos", language="ko"):
  API_KEY = os.getenv("API_KEY")
  
  url = f"https://api.content.tripadvisor.com/api/v1/location/search?key={API_KEY}&searchQuery={query}&category={category}&language={language}"
  headers = {"accept": "application/json"}
  
  try:
    time.sleep(0.1)
    response = requests.get(url, headers=headers)
    return response.text
  except Exception as e:
    # raise e
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
  file_path = "resources/output.txt"
  
  text_store = chroma.VectorStore(os.getenv("LONGFORMER"), text_model_type, text_collection_name)
  image_store = chroma.VectorStore(os.getenv("CLIP"), image_model_type, image_collection_name)
  
  cache = {}
  
  divider = get_division()
  reader = file_read(file_path)
  next(divider)
  db = next(getDB())
  
  cnt = 0
  while (line := next(reader)):
    data = parse_json(search_location(line.strip()))
    
    if not data:
      continue
    
    for location in data:
      tracer = trace_route(db)
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
      
      address_obj.append((1e9, location["name"]))
      flag = True
      
      for _, area in address_obj:
        if not tracer.send(area):
          flag = False
          break
        next(tracer)
      if flag:
        cache[location["name"]] = location
        tracer.close()
        continue
      
      next(tracer)
      parent = tracer.send(None)

      _location = parse_json(get_location(location["location_id"]))
      location = create_location(Location(
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
      resort = create_resort(Resort(
        location_id=location.id,
        name=_location["name"],
        alias=_location["location_id"],
        description=(_location["description"] if "description" in _location else _location["name"]),
      ))
      
      if "description" in _location:
        text_store.store(
          texts=[_location["description"]],
          metas=[{"id": resort.id, "name": resort.name}]
        )
      
      print(f"location: {location.name} -> resort: {resort}")
      cnt += 1
      
      if cnt % 1000 == 0:
        print(f"cnt: {cnt}")
        return

if __name__ == "__main__":
  load_dotenv("../../.env")
  
  main()