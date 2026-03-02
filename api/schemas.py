from pydantic import BaseModel, EmailStr, ConfigDict, Field
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
    creator_employee_id: int
    updated_employee_id: int
    created_at: datetime
    updated_at: datetime
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
    creator_employee_id: int
    updated_employee_id: int
    created_at: datetime
    updated_at: datetime
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
    creator_employee_id: int
    updated_employee_id: int
    created_at: datetime
    updated_at: datetime
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
    image_url: Optional[str] = None
    created_at: datetime

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
    stops: list[int]
    image_url: Optional[str] = None
    creator_employee_id: int
    updated_employee_id: int
    model_config = ConfigDict(from_attributes=True)

class RateRequest(BaseModel):
    exhibit_id: int
    rating: bool  # True = like, False = dislike

# Home
class HomeCardOut(BaseModel):
    id: int
    title: str
    image_url: Optional[str] = None
    description: str


class HomeResponse(BaseModel):
    top_section: List[HomeCardOut] = []
    mid_section: Optional[HomeCardOut] = None
    bottom_section: List[HomeCardOut] = []

class BeaconEventIn(BaseModel):
    beacon_uuid: str
    beacon_major: int
    beacon_minor: int
    rssi: int
    tx_power: int
    recorded_at: int = Field(..., description="Epoch milliseconds from device")

class BeaconEventsRequest(BaseModel):
    session_id: str
    events: List[BeaconEventIn]