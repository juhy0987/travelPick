


def main():
  division = [set(), set(),set(),set(),set()]
  
  with open("citylist.txt", "r") as f:
    lines = f.read().split("\n")
  
  for line in lines:
    area = line.strip().split(",")
    area.reverse()
    
    for i, a in enumerate(area):
      division[i].add(a)
    
  with open("output.txt", "w") as f:
    for l in division:
      for a in l:
        f.write(a + "\n")

if __name__ == "__main__":
  main()