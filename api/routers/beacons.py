from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from api.db.database import get_db
from api.db.models import Beacon, User
from api.schemas.beacons import BeaconCreate, BeaconRead, BeaconUpdate
from api.core.auth import get_current_user

router = APIRouter()

@router.get("", response_model= list[BeaconRead])
def get_beacons(db: Session = Depends(get_db)):
    return db.query(Beacon).all()

@router.post("", response_model=BeaconRead, status_code=201)
def create_beacon(data: BeaconCreate, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    beacon = Beacon(**data.model_dump(), creator_employee_id=current_user.id, updated_employee_id=current_user.id)
    db.add(beacon)
    db.commit()
    db.refresh(beacon)
    return beacon

@router.get("/{beacon_id}", response_model= BeaconRead)
def get_beacon(beacon_id: int, db: Session = Depends(get_db)):
    beacon = db.query(Beacon).filter(Beacon.id == beacon_id).first()
    if beacon is None:
        raise HTTPException(status_code=404, detail="Beacon could not be found.")
    return beacon

@router.put("/{beacon_id}", response_model=BeaconRead)
def update_beacon(beacon_id: int, data: BeaconUpdate, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    beacon = db.query(Beacon).filter(Beacon.id == beacon_id).first()
    if beacon is None:
        raise HTTPException(status_code=404, detail="Beacon could not be found.")
    for key, value in data.model_dump(exclude_unset=True).items():
        setattr(beacon, key, value)
    beacon.updated_employee_id = current_user.id
    db.commit()
    db.refresh(beacon)
    return beacon