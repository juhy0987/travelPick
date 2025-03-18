import time
import re
import requests
import subprocess
import uuid
from selenium import webdriver
from selenium.webdriver.chrome.options import Options

class RemoteConnector:
  def __init__(
    self, 
    url="http://localhost:{}/wd/hub", 
    headless=True
  ):
    
    self.uuid = uuid.uuid4()
    self.container_name = f"chrome-{self.uuid}"
    self.base_url = url
    
    if not self.init_container():
      return None
    if not self.wait_for_container():
      return None
    if not self.get_container_port():
      return None
    
    # 원격 Chrome 설정
    self.chrome_options = Options()
    if headless:
      self.chrome_options.add_argument("--headless")
    self.chrome_options.add_argument("--no-sandbox")
    self.chrome_options.add_argument("--disable-dev-shm-usage")
    
    self.driver = None
  
  def init_container(self):
    
    result = subprocess.run(["docker", "run", "-d", "--name", self.container_name, \
      "-p", "0:4444", \
      "-p", "0:7900", \
      "selenium/standalone-chrome"],
      capture_output=True, text=True)
    
    return result.returncode == 0
  
  def wait_for_container(self, timeout=60):
    start_time = time.time()
    while time.time() - start_time < timeout:
      result = subprocess.run(["docker", "inspect", "-f", "{{.State.Running}}", self.container_name],
                              capture_output=True, text=True)
      if result.stdout.strip() == "true":
        print("Docker container is running.")
        return True
      time.sleep(1)
    print("Timeout waiting for Docker container to start.")
    return False
  
  def get_container_port(self):
    
    result = subprocess.run(["docker", "port", self.container_name], 
      capture_output=True, 
      text=True)
    
    if result.returncode != 0:
      return {}
    
    ports = {}
    for line in result.stdout.strip().split("\n"):
      match = re.match(r"(\d+)/tcp -> 0.0.0.0:(\d+)", line)
      if match:
        ports[int(match.group(1))] = int(match.group(2))
    
    self.ports = ports
    return ports

  async def connect_driver(self, timeout=10):
    if self.driver: # 이미 연결되어 있으면
      return True

    start_time = time.time() # Time out 설정
    while time.time() - start_time < timeout:
      try:
        # Remote WebDriver 연결 (Docker 내부 Chrome 사용)
        self.driver = webdriver.Remote(
          command_executor=self.base_url.format(self.get_selenium_port()),
          options=self.chrome_options
        )
      except Exception as e:
        time.sleep(1) # 실패 시 1초 대기 후 재시도
        continue
      print("Connected")
      return True # 연결 시 True 반환

    print(f"Time out to connect driver {self.container_name}")
    return False # 연결 실패
  
  def get_uuid(self):
    return self.uuid
  
  def get_selenium_port(self):
    
    return self.ports.get(4444)

  def get_vnc_port(self):
    
    return self.ports.get(7900)
  
  def get(self, url: str):
    
    return self.driver.get(url)

  def quit(self):
    if self.driver:
      self.driver.quit()
    self.driver = None
  
  def exit(self):
    
    subprocess.run(["docker", "stop", self.container_name],
      stdout=subprocess.DEVNULL,
      stderr=subprocess.DEVNULL)
    subprocess.run(["docker", "rm", self.container_name],
      stdout=subprocess.DEVNULL,
      stderr=subprocess.DEVNULL)

  def __del__(self):
    try:
      if self.driver:
        self.driver.quit()
      self.exit()
      print("exit perfectly")
    except:
      print("del error")
      pass
  
  def __lt__(self, other):
    return self.uuid < other.uuid
  
  # @classmethod
  # def quit_all_driver(cls):
  #   print("quit all driver")
  #   result = subprocess.run(["docker", "ps", "-a", "--filter", "name=chrome", "--format", "{{.ID}}"],
  #                           stdout=subprocess.PIPE, stderr=subprocess.PIPE)
  #   container_ids = result.stdout.decode().strip().split("\n")
    
  #   if container_ids:
  #     subprocess.run(["docker", "stop"] + container_ids, 
  #       stdout=subprocess.PIPE, stderr=subprocess.PIPE)
  #     subprocess.run(["docker", "rm"] + container_ids, 
  #       stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    

if __name__ == "__main__":
  connector = RemoteConnector()
  connector.connect_driver()
  connector.get("https://www.google.com")
  connector.quit()
  del connector