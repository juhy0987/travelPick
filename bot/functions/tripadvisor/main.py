import os
import requests
import json
from dotenv import load_dotenv
from shapely.geometry import Point
from geoalchemy2.shape import from_shape

from db.crud import *


def get_location(location_id, language="ko"):
  API_KEY = os.getenv("API_KEY")
  
  url = f"https://api.content.tripadvisor.com/api/v1/location/{location_id}/details?language={language}&currency=USD&key={API_KEY}"
  headers = {"accept": "application/json"}
  
  try:
    response = requests.get(url, headers=headers)
    return response.text
  except Exception as e:
    return ""

def search_location(query, category="geos", language="ko"):
  API_KEY = os.getenv("API_KEY")
  
  url = f"https://api.content.tripadvisor.com/api/v1/location/search?key={API_KEY}&searchQuery={query}&category={category}&language={language}"
  headers = {"accept": "application/json"}
  
  try:
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
    yield f.readline()
    
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
  cache = {}
  
  divider = get_division()
  reader = file_read("output.txt")
  next(divider)
  
  cnt = 0
  while (line := next(reader)):
    tracer = trace_route()
    next(tracer)
    
    data = parse_json(search_location(line.strip()))
    
    if not data:
      continue
    
    for location in data:
      address = location["address_obj"]
      address_obj = []
      
      for division, area in address.items():
        division = divider.send(division)
        if not division:
          continue
        
        address_obj.append((division, area))
      address_obj.sort()
      
      flag = False
      for area in address_obj:
        if not tracer.send(area):
          flag = True
          break
      if flag:
        cache[location["name"]] = location
        continue
      
      parent = tracer.send(None)
      _location = parse_json(get_location(location["location_id"]))
      location = create_location(Location(
        parent_id=parent,
        name=_location["name"],
        alias=None,
        coordinates=from_shape(
          Point(_location['latitude'], _location['longitude']), 
          srid=4326
        ),
        timezone=_location["timezone"]
      ))
      
      resort = create_resort(Resort(
        location_id=location.id,
        name=_location["name"],
        alias=_location["alias"],
        description=_location["description"],
      ))
    
  

if __name__ == "__main__":
  load_dotenv("../../.env")
  
  main()