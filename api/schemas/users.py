from pydantic import BaseModel, EmailStr, ConfigDict
from typing import Optional

'''
Contains all the user schemas
'''

class UserCreate(BaseModel):
    first_name: str
    surname: str
    email: EmailStr
    password: Optional[str] = None

class UserRead(BaseModel):
    id: int
    first_name: str
    surname: str
    email: EmailStr
    model_config = ConfigDict(from_attributes=True)

class UserUpdate(BaseModel):
    first_name: Optional[str] = None
    surname: Optional[str] = None
    email: Optional[EmailStr] = None