import importlib

from . import google

__methods__ = [
  "get_review"
]

def __getattr__(name):
  if name in __methods__:
    def wrapper(source):
      return _method(source, name)
    return wrapper
  raise AttributeError(f"module 'review' has no attribute '{name}'")


def _method(source, method_name):
  def decorator(func):
    def decorated(*args, **kwargs):
      try:
        module = importlib.import_module(f'.{source}', package='review')
        class_ = getattr(module, source.capitalize())
        method = getattr(class_, method_name)
        return method(func)(*args, **kwargs)
      except (ModuleNotFoundError, AttributeError) as e:
        raise ValueError(f"Error in finding method {method_name} in source {source}: {e}")
    return decorated
  return decorator