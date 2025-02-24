import atexit
import time

import review
from manager import Manager
from processor import Processor
from remoteConnector import RemoteConnector

def init():
  max_child = 4
  manager = Manager(max_child, platforms=["google"])
  atexit.register(lambda: manager.quit())
  
  for _ in range(max_child):
    manager.add_child()
  
  return manager

def run(manager):
  while True:
    url = input("URL: ")
    if url == "exit":
      break
    manager.set_work(url)
  
  del manager

def main():
  init()
  
  time.sleep(3)
  

def test():
  processor = Processor(RemoteConnector(), ["google"])
  processor.connector.connect_driver()
  while(True):
    if processor.connector.connect_driver():
      processor.methods["google"]["get_review"]("https://www.google.com")
    time.sleep(3)
    
  del processor
  


if __name__ == "__main__":
  main()
  # test()