from pydantic import BaseModel, ConfigDict
from datetime import datetime

'''
Contains all the category schemas
'''

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