import uuid
from sqlalchemy import Column, Integer, Double, String, Boolean, DateTime, ForeignKey, LargeBinary, Index, TupleType
from sqlalchemy.orm import relationship
from sqlalchemy.ext.declarative import declarative_base
from geoalchemy2 import Geometry
from shapely.geometry import Point

Base = declarative_base()

# Table
class User(Base):
  __tablename__ = 'user'
  
  email = Column(String(255), primary_key=True)
  pass_hash = Column(String(255), nullable=False)
  name = Column(String(255), nullable=False)
  refresh_token = Column(String(255), nullable=True)
  created = Column(DateTime, nullable=False)
  
  review = relationship('Review', back_populates='user')

class Location(Base):
  __tablename__ = 'location'
  
  id = Column(String(36), primary_key=True, default=uuid.uuid4)
  parent_id = Column(String(36), ForeignKey('location.id'), nullable=True)
  name = Column(String(255), nullable=False)
  alias = Column(String(255), nullable=True)
  coordinates = Column(Geometry(geometry_type='POINT', srid=4326), nullable=False)
  timezone = Column(Integer, nullable=False)
  
  Index('parent_id', 'name')

  resort = relationship('Resort', back_populates='location')
  
class Resort(Base):
  __tablename__ = 'resort'
  
  id = Column(String(36), primary_key=True, default=uuid.uuid4)
  location_id = Column(String(36), ForeignKey('location.id'), nullable=False)
  name = Column(String(255), nullable=False)
  alias = Column(String(255), nullable=True)
  description = Column(String, nullable=True)
  
  Index('location_id', 'name')
  
  location = relationship('Location', back_populates='resort')
  review = relationship('Review', back_populates='resort')
  photo = relationship('Photo', back_populates='resort')
  
class Review(Base):
  __tablename__ = 'review'
  
  id = Column(String(36), primary_key=True, default=uuid.uuid4)
  resort_id = Column(String(36), ForeignKey('resort.id'), nullable=False)
  user_id = Column(String(255), ForeignKey('user.email'), nullable=False)
  content = Column(String, nullable=False)
  created = Column(DateTime, nullable=False)
  updated = Column(DateTime, nullable=False)
  
  Index('resort_id')
  
  resort = relationship('Resort', back_populates='review')
  user = relationship('User', back_populates='review')
  photo = relationship('Photo', back_populates='review')
  
class Photo(Base):
  __tablename__ = 'photo'
  
  id = Column(String(36), primary_key=True, default=uuid.uuid4)
  resort_id = Column(String(36), ForeignKey('resort.id'), nullable=True)
  review_id = Column(String(36), ForeignKey('review.id'), nullable=True)
  data = Column(LargeBinary, nullable=False)
  ext = Column(String(255), nullable=False)
  
  Index('resort_id', 'review_id')
  
  resort = relationship('Resort', back_populates='photo')
  review = relationship('Review', back_populates='photo')