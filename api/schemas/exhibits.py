from pydantic import BaseModel, ConfigDict
from datetime import datetime
from typing import Optional

'''
Contains all the exhibit schemas
'''

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
    is_loud: bool
    is_crowded: bool
    is_dark: bool
    beacon_id: Optional[int] = None
    category_id: Optional[int] = None
    image_url: Optional[str] = None
    creator_employee_id: int
    updated_employee_id: int
    created_at: datetime
    updated_at: datetime
    model_config = ConfigDict(from_attributes=True)

class ExhibitUpdate(BaseModel):
    title: Optional[str] = None
    description: Optional[str] = None
    child_friendly: Optional[bool] = None
    is_loud: Optional[bool] = None
    is_crowded: Optional[bool] = None
    is_dark: Optional[bool] = None
    beacon_id: Optional[int] = None
    category_id: Optional[int] = None
    image_url: Optional[str] = None

class ExhibitReadPublic(BaseModel):
    id: int
    title: str
    description: str
    category: str
    child_friendly: bool
    is_loud: bool
    is_crowded: bool
    is_dark: bool
    likes: int
    dislikes: int
    uuid: str
    major: int
    minor: int
    location: int
    image_url: Optional[str] = None
    created_at: datetime

    model_config = ConfigDict(from_attributes=True)

class RateRequest(BaseModel):
    exhibit_id: int
    rating: bool  # True = like, False = dislike