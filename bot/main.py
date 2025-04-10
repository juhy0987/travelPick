# from functions.crawler import main as crawler
from functions.tripadvisor import main as tripadvisor

if __name__ == "__main__":
  # crawler.main()
  # tripadvisor.main()
  results = tripadvisor.load_image(2554430)
  print(results)