from fastapi import APIRouter, Depends, HTTPException, Response
from sqlalchemy.orm import Session

from api.db.database import get_db
from api.db.models import Beacon, User, ChangeType
from api.schemas.beacons import BeaconCreate, BeaconRead, BeaconUpdate
from api.core.auth import get_current_user
from api.core.changelog import log_change

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
    log_change(db, ChangeType.CREATE, "beacon", {"id": beacon.id, "name": beacon.name, "description": beacon.description}, current_user.id)
    return beacon

@router.get("/{beacon_id}", response_model= BeaconRead)
def get_beacon(beacon_id: int, db: Session = Depends(get_db)):
    beacon = db.query(Beacon).filter(Beacon.id == beacon_id).first()
    if beacon is None:
        raise HTTPException(status_code=404, detail="Beacon could not be found.")
    return beacon

@router.delete("/{beacon_id}", status_code=204)
def delete_beacon(beacon_id: int, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    beacon = db.query(Beacon).filter(Beacon.id == beacon_id).first()
    if beacon is None:
        raise HTTPException(status_code=404, detail="Beacon could not be found.")
    log_change(db, ChangeType.DELETE, "beacon", {"id": beacon.id, "name": beacon.name}, current_user.id)
    db.delete(beacon)
    db.commit()
    return Response(status_code=204)

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
    log_change(db, ChangeType.UPDATE, "beacon", {"id": beacon.id, "name": beacon.name, "changes": data.model_dump(exclude_unset=True)}, current_user.id)
    return beacon