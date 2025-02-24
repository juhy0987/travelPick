import asyncio
import importlib
import uuid

import review
from remoteConnector import RemoteConnector

class Processor:
  def __init__(self, connector: RemoteConnector, platforms: list[str] = []):
    self.connector = connector
    
    asyncio.run(self.connector.connect_driver())
    
    self.methods = {}
    
    for platform in platforms:
      try:
        self.add_methods(platform)
      except:
        pass
    
  def add_methods(self, source):
    if self.methods.get(source) is None:
      self.methods[source] = {}
    
    try:
      module = importlib.import_module(f'.{source}', package='review')
      class_ = getattr(module, source.capitalize())
      for method_name in dir(class_):
        if callable(getattr(class_, method_name)) and not method_name.startswith("__"):
          async def func(*args, **kwargs):
            return self.get(*args, **kwargs)
          
          self.methods[source][method_name] = getattr(class_, method_name)(self.get)
    except (ModuleNotFoundError, AttributeError) as e:
      raise ValueError(f"Error in finding methods in source {source}: {e}")
  
  def get(self, url: str):
    if not self.connector:
      return ""
    return self.connector.get(url)

  def get_connector(self):
    return self.connector

  def quit(self):
    if self.connector:
      self.connector.quit()
    self.connector = None
    
  def __del__(self):
    self.quit()
    