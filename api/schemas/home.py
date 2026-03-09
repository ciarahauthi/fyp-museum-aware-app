from pydantic import BaseModel, Field
from typing import Optional, List

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