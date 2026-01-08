from fastapi import FastAPI, Depends, HTTPException
from sqlalchemy.orm import Session
from database import get_db
from models import *
from schemas import *

app = FastAPI()

@app.get("/")
async def root():
    return {"message": "Hello World"}

# User queries
@app.post("/api/users/", response_model=UserRead)
def create_user(
    data: UserCreate,
    db: Session = Depends(get_db)
):
    # Check if user already exists
    existingUser = db.query(User).filter(User.email == data.email).first()
    if existingUser:
        raise HTTPException(status_code=400, detail="Email already in use")
    
    user = User(
        first_name = data.first_name, 
        surname = data.surname, 
        email = data.email
    )
    
    db.add(user)
    db.commit()
    db.refresh(user)
    return user

@app.get("/api/users/", response_model= list[UserRead])
def get_users(db: Session = Depends(get_db)):
    return db.query(User).all()