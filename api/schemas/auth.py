from pydantic import BaseModel, EmailStr

'''
Contains all the auth schemas
'''

# Auth
class UserRegister(BaseModel):
    first_name: str
    surname: str
    email: EmailStr
    password: str

class Token(BaseModel):
    access_token: str
    token_type: str