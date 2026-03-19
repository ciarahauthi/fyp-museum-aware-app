from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy import tuple_
from sqlalchemy.orm import Session

from api.db.database import get_db
from api.db.models import User, ChangeType
from api.schemas.users import UserRead, UserCreate, UserUpdate
from api.core.auth import get_current_user, hash_password
from api.core.changelog import log_change

router = APIRouter()

# POST
@router.post("", response_model=UserRead)
def create_user(
    data: UserCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    # Check if user already exists
    existingUser = db.query(User).filter(User.email == data.email).first()
    if existingUser:
        raise HTTPException(status_code=400, detail="Email already in use")

    user = User(
        first_name=data.first_name,
        surname=data.surname,
        email=data.email,
        password_hash=hash_password(data.password) if data.password else None,
    )

    db.add(user)
    db.commit()
    db.refresh(user)
    log_change(db, ChangeType.CREATE, "users", {"id": user.id, "email": user.email}, current_user.id)
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

@router.put("/{user_id}", response_model=UserRead)
def update_user(user_id: int, data: UserUpdate, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    user = db.query(User).filter(User.id == user_id).first()
    if user is None:
        raise HTTPException(status_code=404, detail="User could not be found.")
    for key, value in data.model_dump(exclude_unset=True).items():
        setattr(user, key, value)
    db.commit()
    db.refresh(user)
    log_change(db, ChangeType.UPDATE, "users", {"id": user.id, "email": user.email, "changes": data.model_dump(exclude_unset=True)}, current_user.id)
    return user