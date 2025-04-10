from sqlalchemy.orm import Session

from db.database import *
from db.model import *
from db.crud import *
from db.schema import *


class Transaction:
  def __init__(self, func):
    self.func = func
  
  def __call__(self, db, *args, **kwargs):
    try:
      result = self.func(db, *args, **kwargs)
    except Exception as e:
      db.rollback()
      raise e
    finally:
      db.commit()
      db.refresh(result)
    return result
  

def get_location(db, location_id: str):
  
  return _get_location(db, location_id)

def search_location(db, searchSchema: LocationSearchSchema):
  
  return _search_location(db, searchSchema)

def trace_route(db):
  
  return _trace_route(db)

def get_resort(db, resort_id: str):
  
  return _get_resort(db, resort_id)

def search_resort(db, searchSchema: ResortSearchSchema):
  
  return _search_resort(db, searchSchema)


def _get_location(db: Session, location_id: str):
  
  return db.query(Location) \
    .filter(Location.id == location_id) \
    .first()

def _search_location(db: Session, searchSchema: LocationSearchSchema):
  
  query = db.query(Location)
  if searchSchema.parent_id:
    query = query.filter(Location.parent_id == searchSchema.parent_id)
  if searchSchema.name:
    query = query.filter(Location.name == searchSchema.name)
  if searchSchema.alias:
    query = query.filter(Location.alias == searchSchema.alias)
  if searchSchema.coordinates:
    query = query.filter(Location.coordinates == searchSchema.coordinates)
  if searchSchema.timezone:
    query = query.filter(Location.timezone == searchSchema.timezone)
  
  return query.all()

def _get_resort(db: Session, resort_id: str):
  
  return db.query(Resort) \
    .filter(Resort.id == resort_id) \
    .first()

def _search_resort(db: Session, searchSchema: ResortSearchSchema):
  
  query = db.query(Resort)
  if searchSchema.location_id:
    query = query.filter(Resort.location_id == searchSchema.location_id)
  if searchSchema.name:
    query = query.filter(Resort.name == searchSchema.name)
  if searchSchema.alias:
    query = query.filter(Resort.alias == searchSchema.alias)
  if searchSchema.description:
    query = query.filter(Resort.description == searchSchema.description)
  
  return query.all()

def _trace_route(db: Session):
  
  parent = None
  while True:
    cur = yield
    if not cur:
      yield parent
      continue
        
    
    location = _search_location(db, Location(parent_id=parent, name=cur))
    if not location:
      yield False
      continue
    else:
      location = location[0]
    
    parent = location.id
    yield True
  

def _create_location(db: Session, location: Location):
  
  db.add(location)
  return location

def _create_resort(db: Session, resort: Resort):
  
  db.add(resort)
  return resort

def _create_photo(db: Session, photo: Photo):
  
  db.add(photo)
  return photo


@Transaction
def create_location(db: Session, location: Location):
  
  return _create_location(db, location)

@Transaction
def create_resort(db: Session, resort: Resort):
  
  return _create_resort(db, resort)

@Transaction
def create_photo(db: Session, photo: Photo):
  
  return _create_photo(db, photo)
