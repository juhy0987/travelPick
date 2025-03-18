
class Google:
  
  @classmethod
  def get_review(cls, func):
    def decorated(*args, **kwargs):
      # before
      print("before")
      
      result = func(*args, **kwargs)
      
      # after
      print("after")
      return result
    
    return decorated