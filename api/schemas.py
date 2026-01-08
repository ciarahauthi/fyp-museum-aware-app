from pydantic import BaseModel, EmailStr, ConfigDict

class UserCreate(BaseModel):
    first_name: str
    surname: str
    email: EmailStr

class UserRead(UserCreate):
    id: int
    model_config = ConfigDict(from_attributes=True)