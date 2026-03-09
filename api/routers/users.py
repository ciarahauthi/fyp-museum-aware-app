from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy import tuple_
from sqlalchemy.orm import Session

from api.db.database import get_db
from api.db.models import User
from api.schemas.users import UserRead, UserCreate

router = APIRouter()

# POST
@router.post("", response_model=UserRead)
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

@router.get("", response_model= list[UserRead])
def get_users(db: Session = Depends(get_db)):
    return db.query(User).all()

@router.get("/{user_id}", response_model= UserRead)
def get_user(user_id: int, db: Session = Depends(get_db)):
    user = db.query(User).filter(User.id == user_id).first()

    if user is None:
        raise HTTPException(status_code=404, detail="User could not be found.")
    return user