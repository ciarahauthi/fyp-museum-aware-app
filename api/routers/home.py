from fastapi import APIRouter, Depends, HTTPException, Request
from sqlalchemy.orm import Session

from api.db.database import get_db
from api.db.models import Home
from api.schemas.home import HomeResponse, HomeResponseAdmin, HomeCreate, HomeUpdate
from api.core.auth import get_current_user

router = APIRouter()

@router.get("", response_model=HomeResponse)
def get_home(request: Request, db: Session = Depends(get_db)):
    homes = (
        db.query(Home)
        .filter(Home.active == True)
        .order_by(Home.section.asc(), Home.id.desc())
        .all()
    )

    base = str(request.base_url).rstrip("/")

    top_section = []
    bottom_section = []
    mid_section = None

    for h in homes:
        image_url = h.image_url
        if image_url and not image_url.startswith("http"):
            image_url = base + image_url

        card = {
            "id": h.id,
            "title": h.title,
            "image_url": image_url,
            "description": h.description
        }

        if h.section.value == "top":
            top_section.append(card)
        elif h.section.value == "mid":
            if mid_section is None:
                mid_section = card
        elif h.section.value == "bottom":
            bottom_section.append(card)

    return {
        "top_section": top_section,
        "mid_section": mid_section,
        "bottom_section": bottom_section
    }

@router.get("/admin", response_model=list[HomeResponseAdmin])
def get_home_admin(db: Session = Depends(get_db)):
    return db.query(Home).all()

@router.post("", response_model=HomeResponseAdmin)
def create_home(data: HomeCreate, db: Session = Depends(get_db), current_user=Depends(get_current_user)):
    home = Home(
        title=data.title,
        description=data.description,
        section=data.section,
        active=data.active,
        image_url=data.image_url,
        creator_employee_id=current_user.id,
        updated_employee_id=current_user.id,
    )
    db.add(home)
    db.commit()
    db.refresh(home)
    return home

@router.put("/{home_id}", response_model=HomeResponseAdmin)
def update_home(home_id: int, data: HomeUpdate, db: Session = Depends(get_db), current_user=Depends(get_current_user)):
    home = db.query(Home).filter(Home.id == home_id).first()
    if home is None:
        raise HTTPException(status_code=404, detail="Home item not found.")
    for key, value in data.model_dump(exclude_unset=True).items():
        setattr(home, key, value)
    home.updated_employee_id = current_user.id
    db.commit()
    db.refresh(home)
    return home