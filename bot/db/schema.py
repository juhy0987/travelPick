from pydantic import BaseModel
from typing import Optional, Union
from datetime import datetime

# DB Models
def LocationSearchSchema(BaseModel):
  parent_id: Optional[int] = None
  name: Optional[str] = None
  alias: Optional[str] = None
  coordinates: Optional[str] = None
  timezone: Optional[str] = None