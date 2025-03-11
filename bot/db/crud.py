from sqlalchemy.orm import Session

from db.database import *
from db.model import *
from db.crud import *
from db.schema import *


def get_location(db: Session, location_id: int):
  
  return db.query(Location) \
    .filter(Location.id == location_id) \
    .first()

def search_location(db: Session, searchSchema: LocationSearchSchema):
  
  query = db.query(Location)
  if searchSchema.parent_id:
    query = query.filter(Location.parent_id == searchSchema.parent_id)
  if searchSchema.resort_id:
    query = query.filter(Location.resort_id == searchSchema.resort_id)
  if searchSchema.name:
    query = query.filter(Location.name == searchSchema.name)
  if searchSchema.alias:
    query = query.filter(Location.alias == searchSchema.alias)
  if searchSchema.coordinates:
    query = query.filter(Location.coordinates == searchSchema.coordinates)
  if searchSchema.timezone:
    query = query.filter(Location.timezone == searchSchema.timezone)
  
  return query.all()

def trace_route(db: Session):
  
  parent = None
  while True:
    cur = yield
    if not cur:
      break
    
    location = search_location(db, Location(parent_id=parent, name=cur))
    if not location:
      yield False
    else:
      location = location[0]
    
    parent = location.id
    yield True
  
  yield parent



class Transaction:
  def __init__(self, func):
    self.func = func
  
  def __call__(self, *args, **kwargs):
    try:
      db = next(getDB())
      result = self.func(db, *args, **kwargs)
    except Exception as e:
      db.rollback()
      raise e
    finally:
      db.commit()
      db.refresh(result)
    return result
  

def _create_location(db: Session, location: Location):
  
  target = db.query(Location) \
    .filter(location.name == Location.name) \
    .filter(location.parent_id == Location.parent_id) \
    .first()
  if target:
    return target
  
  db.add(location)
  return target

def _create_resort(db: Session, resort: Resort):
  
  target = db.query(Resort) \
    .filter(resort.name == Resort.name) \
    .filter(resort.location_id == Resort.location_id) \
    .first()
  if target:
    return target
  
  db.add(resort)
  return target


@Transaction
def create_location(db: Session, location: Location):
  
  return _create_location(db, location)

@Transaction
def create_resort(db: Session, resort: Resort):
  
  return _create_resort(db, resort)

