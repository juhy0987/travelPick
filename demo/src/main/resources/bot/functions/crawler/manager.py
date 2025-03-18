import asyncio
import heapq
import subprocess
import time

from processor import Processor
from remoteConnector import RemoteConnector

class Manager:
  def __init__(self, max_child=4, platforms=[]):
    if max_child < 1:
      raise ValueError("max_child must be greater than 0")
    
    self.max_child = max_child
    self.platforms = platforms
    
    self.childs = []
    self.pools = []
    
    for _ in range(max_child):
      heapq.heappush(self.pools, (False, RemoteConnector()))
    
  def add_child(self):
    if len(self.childs) >= self.max_child:
      return False
    if self.pools[0][0]:
      return False

    _, connector = heapq.heappop(self.pools)
    new_child = Processor(connector, self.platforms)
    self.childs.append(new_child)
    heapq.heappush(self.pools, (True, connector))
    return True

  async def set_work(self, url, timeout=10):
    if not self.childs:
      return False
    if self.childs[0][0]:
      return False
    
    child = self.childs.pop(0)
    
    try:
      await asyncio.wait_for(child.get(url), timeout)
    except asyncio.TimeoutError:
      connector = child.get_connector()
      self.del_connector(connector)
      
      del child
      
      self.add_connector()
      self.add_child()
      return False
    
    self.childs.append(child)
    return True
  
  def add_connector(self):
    if len(self.pools) >= self.max_child:
      return False
    
    heapq.heappush(self.pools, (False, RemoteConnector()))
    return True
  
  def del_connector(self, target):
    for i, (_, connector) in enumerate(self.pools):
      if target == connector:
        _, tmp = self.pools.pop(i)
        del tmp
        return True
    
    return False

  def quit(self):
    for child in self.childs:
      child.quit()
    for _, connector in self.pools:
      connector.exit()
    
    result = subprocess.run(["docker", "ps", "-a", "--filter", "name=chrome", "--format", "{{.ID}}"],
                            stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    container_ids = result.stdout.decode().strip().split("\n")
    for _, connector in self.pools:
      if connector.get_uuid() in container_ids:
        connector.exit()
        time.sleep(0.5)