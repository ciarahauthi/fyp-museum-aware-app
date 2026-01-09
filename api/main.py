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

@app.get("/api/users/{user_id}", response_model= UserRead)
def get_user(user_id: int, db: Session = Depends(get_db)):
    user = db.query(User).filter(User.id == user_id).first()

    if user is None:
        raise HTTPException(status_code=404, detail="User could not be found.")
    return user

# Beacon Queries
@app.get("/api/beacons/", response_model= list[BeaconRead])
def get_beacons(db: Session = Depends(get_db)):
    return db.query(Beacon).all()

@app.get("/api/beacons/{beacon_id}", response_model= BeaconRead)
def get_beacon(beacon_id: int, db: Session = Depends(get_db)):
    beacon = db.query(Beacon).filter(Beacon.id == beacon_id).first()

    if beacon is None:
        raise HTTPException(status_code=404, detail="Beacon could not be found.")
    return beacon

# Exhibit Queries
@app.get("/api/exhibits/", response_model= list[ExhibitRead])
def get_exhibits(db: Session = Depends(get_db)):
    return db.query(Exhibit).all()

@app.get("/api/exhibits/{exhibit_id}", response_model= ExhibitRead)
def get_exhibit(exhibit_id: int, db: Session = Depends(get_db)):
    exhibit = db.query(Exhibit).filter(Exhibit.id == exhibit_id).first()

    if exhibit is None:
        raise HTTPException(status_code=404, detail="Exhibit could not be found.")
    return exhibit

# Category Queries
@app.get("/api/categories/", response_model= list[CategoryRead])
def get_categories(db: Session = Depends(get_db)):
    return db.query(Category).all()

@app.get("/api/categories/{category_id}", response_model= CategoryRead)
def get_category(category_id: int, db: Session = Depends(get_db)):
    category = db.query(Category).filter(Category.id == category_id).first()

    if category is None:
        raise HTTPException(status_code=404, detail="Category could not be found.")
    return category