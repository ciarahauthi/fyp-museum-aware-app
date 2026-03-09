from fastapi import APIRouter, Depends, HTTPException, status, Response, Request, Query
from sqlalchemy.orm import Session
from sqlalchemy import tuple_
from typing import Annotated
from datetime import timezone

from api.db.database import get_db
from api.db.models import Exhibit, Beacon, Category
from api.schemas.exhibits import ExhibitRead, ExhibitReadPublic
from api.schemas.exhibits import RateRequest

router = APIRouter()

# Exhibit Queries
@router.get("", response_model=list[ExhibitReadPublic])
def get_exhibits(request: Request, db: Session = Depends(get_db)):
    exhibits = db.query(Exhibit).all()
    base = str(request.base_url).rstrip("/")

    for e in exhibits:
        if e.image_url and not e.image_url.startswith("http"):
            e.image_url = base + e.image_url

        # JSON includes +00:00
        if e.created_at and e.created_at.tzinfo is None:
            e.created_at = e.created_at.replace(tzinfo=timezone.utc)

    return exhibits

@router.get("/lookup", response_model= ExhibitRead)
def get_exhibit_by_beacon(beacon_uuid: str, beacon_major: int, beacon_minor: int, db: Session = Depends(get_db)):
    exhibit = db.query(Exhibit).join(Beacon, Exhibit.beacon_id == Beacon.id).filter(Beacon.uuid == beacon_uuid, Beacon.major == beacon_major, Beacon.minor == beacon_minor).first()

    if exhibit is None:
        raise HTTPException(status_code=404, detail="Exhibit could not be found.")
    return exhibit

@router.get("/lookup_many", response_model= list[ExhibitReadPublic])
def get_exhibits_by_beacons(data: Annotated[list[str], Query()], db: Session = Depends(get_db)):
    if not data:
        return []
    
    keys = []
    for beacon in data:
        parts = beacon.split(":")
        if len(parts) != 3:
            raise HTTPException(400, detail=f"Invalid format: {beacon}")

        uuid = parts[0]
        try:
            major = int(parts[1])
            minor = int(parts[2])
        except ValueError:
            raise HTTPException(400, detail=f"Major and/or Minor must be of type Integer.")
        
        keys.append((uuid, major, minor))

    exhibits = (
        db.query(Exhibit)
        .join(Beacon, Exhibit.beacon_id == Beacon.id)
        .outerjoin(Category, Exhibit.category_id == Category.id)
        .filter(tuple_(Beacon.uuid, Beacon.major, Beacon.minor).in_(keys)).all())
    return exhibits

@router.get("/{exhibit_id}", response_model= ExhibitRead)
def get_exhibit(exhibit_id: int, db: Session = Depends(get_db)):
    exhibit = db.query(Exhibit).filter(Exhibit.id == exhibit_id).first()

    if exhibit is None:
        raise HTTPException(status_code=404, detail="Exhibit could not be found.")
    return exhibit

# Rating feature
@router.post("/rate", status_code=status.HTTP_204_NO_CONTENT)
def set_rating(payload: RateRequest, db: Session = Depends(get_db)):
    exhibit = db.get(Exhibit, payload.exhibit_id)
    if exhibit is None:
        raise HTTPException(status_code=404, detail="Exhibit not found")

    if payload.rating:
        exhibit.likes += 1
    else:
        exhibit.dislikes += 1

    db.commit()
    return Response(status_code=status.HTTP_204_NO_CONTENT)