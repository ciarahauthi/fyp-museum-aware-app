from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from api.db.database import get_db
from api.db.models import Beacon
from api.schemas.beacons import BeaconRead

router = APIRouter()

@router.get("", response_model= list[BeaconRead])
def get_beacons(db: Session = Depends(get_db)):
    return db.query(Beacon).all()

@router.get("/{beacon_id}", response_model= BeaconRead)
def get_beacon(beacon_id: int, db: Session = Depends(get_db)):
    beacon = db.query(Beacon).filter(Beacon.id == beacon_id).first()

    if beacon is None:
        raise HTTPException(status_code=404, detail="Beacon could not be found.")
    return beacon