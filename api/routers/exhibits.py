from fastapi import APIRouter, Depends, HTTPException, status, Response, Request, Query
from sqlalchemy.orm import Session
from sqlalchemy import tuple_
from typing import Annotated
from datetime import timezone

from api.db.database import get_db
from api.db.models import Exhibit, Beacon, Category
from api.schemas.exhibits import ExhibitRead, ExhibitReadPublic, ExhibitCreate, ExhibitUpdate
from api.schemas.exhibits import RateRequest
from api.core.auth import get_current_user

router = APIRouter()

@router.post("", response_model=ExhibitRead)
def create_exhibit(data: ExhibitCreate, db: Session = Depends(get_db), current_user=Depends(get_current_user)):
    exhibit = Exhibit(
        title=data.title,
        description=data.description,
        child_friendly=data.child_friendly,
        is_loud=data.is_loud,
        is_crowded=data.is_crowded,
        is_dark=data.is_dark,
        beacon_id=data.beacon_id,
        category_id=data.category_id,
        image_url=data.image_url,
        creator_employee_id=current_user.id,
        updated_employee_id=current_user.id,
    )
    db.add(exhibit)
    db.commit()
    db.refresh(exhibit)
    return exhibit

# Exhibit Queries
# One for mobile app
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

@router.get("/admin", response_model=list[ExhibitRead])
def get_exhibits_admin(db: Session = Depends(get_db)):
    return db.query(Exhibit).all()

@router.get("/{exhibit_id}", response_model= ExhibitRead)
def get_exhibit(exhibit_id: int, db: Session = Depends(get_db)):
    exhibit = db.query(Exhibit).filter(Exhibit.id == exhibit_id).first()
    if exhibit is None:
        raise HTTPException(status_code=404, detail="Exhibit could not be found.")
    return exhibit

@router.put("/{exhibit_id}", response_model=ExhibitRead)
def update_exhibit(exhibit_id: int, data: ExhibitUpdate, db: Session = Depends(get_db), current_user = Depends(get_current_user)):
    exhibit = db.query(Exhibit).filter(Exhibit.id == exhibit_id).first()
    if exhibit is None:
        raise HTTPException(status_code=404, detail="Exhibit could not be found.")
    for key, value in data.model_dump(exclude_unset=True).items():
        setattr(exhibit, key, value)
    exhibit.updated_employee_id = current_user.id
    db.commit()
    db.refresh(exhibit)
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