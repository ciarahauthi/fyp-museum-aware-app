from pydantic import BaseModel, ConfigDict
from typing import Optional

'''
Contains all the route schemas
'''

class NodeRead(BaseModel):
    id: int
    name: str
    x: float
    y: float

class RouteCreate(BaseModel):
    name: str
    description: str
    active: bool = True
    stops: list[int] = []
    image_url: Optional[str] = None

class RouteRead(BaseModel):
    id: int
    name: str
    description: str
    node_ids: list[int]
    stops: list[int]
    image_url: Optional[str] = None
    model_config = ConfigDict(from_attributes=True)

class RouteReadAdmin(BaseModel):
    id: int
    name: str
    description: str
    active: bool
    node_ids: list[int]
    stops: list[int]
    image_url: Optional[str] = None
    creator_employee_id: int
    updated_employee_id: int
    model_config = ConfigDict(from_attributes=True)

class RouteUpdate(BaseModel):
    name: Optional[str] = None
    description: Optional[str] = None
    active: Optional[bool] = None
    stops: Optional[list[int]] = None
    image_url: Optional[str] = None