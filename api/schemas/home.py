from pydantic import BaseModel
from typing import Optional, List
from datetime import datetime
from ..db.models import HomeSection

'''
Contains all the home schemas
'''

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

class HomeCreate(BaseModel):
    title: str
    description: str
    section: HomeSection
    active: bool = True
    image_url: Optional[str] = None


class HomeUpdate(BaseModel):
    title: Optional[str] = None
    description: Optional[str] = None
    section: Optional[HomeSection] = None
    active: Optional[bool] = None
    image_url: Optional[str] = None


class HomeResponseAdmin(BaseModel):
    id: int
    section: HomeSection
    active: bool
    title: str
    description: str
    image_url: Optional[str] = None
    created_at: datetime
    updated_at: datetime
    creator_employee_id: int
    updated_employee_id: int