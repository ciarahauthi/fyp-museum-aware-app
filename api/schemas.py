from pydantic import BaseModel, EmailStr, ConfigDict
from datetime import datetime
from typing import Optional, List

# Users
class UserCreate(BaseModel):
    first_name: str
    surname: str
    email: EmailStr

class UserRead(UserCreate):
    id: int
    model_config = ConfigDict(from_attributes=True)

# Categories
class CategoryCreate(BaseModel):
    name: str
    description: str

class CategoryRead(CategoryCreate):
    id: int
    employee_id: int
    created_at: datetime
    model_config = ConfigDict(from_attributes=True)

# Beacons
class BeaconCreate(BaseModel):
    name: str
    uuid: str
    major: int
    minor: int
    description: str

class BeaconRead(BeaconCreate):
    id: int
    employee_id: int
    created_at: datetime
    model_config = ConfigDict(from_attributes=True)

# Exhibits
class ExhibitCreate(BaseModel):
    title: str
    description: str
    child_friendly: bool
    beacon_id: int
    category_id: int

class ExhibitRead(BaseModel):
    id: int
    title: str
    description: str
    child_friendly: bool
    beacon_id: Optional[int] = None
    category_id: Optional[int] = None
    employee_id: int
    created_at: datetime
    model_config = ConfigDict(from_attributes=True)

class ExhibitReadPublic(BaseModel):
    id: int
    title: str
    description: str
    category: str
    child_friendly: bool
    likes: int
    dislikes: int
    uuid: str
    major: int
    minor: int
    location: int

    model_config = ConfigDict(from_attributes=True)

class NodeRead(BaseModel):
    id: int
    name: str
    x: float
    y: float

class RouteRead(BaseModel):
    id: int
    name: str
    description: str
    node_ids: list[int]

    model_config = ConfigDict(from_attributes=True)