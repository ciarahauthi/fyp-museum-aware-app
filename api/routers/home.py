from fastapi import APIRouter, Depends, Request
from sqlalchemy.orm import Session

from api.db.database import get_db
from api.db.models import Home
from api.schemas.home import HomeResponse

router = APIRouter()

@router.get("", response_model=HomeResponse)
def get_home(request: Request, db: Session = Depends(get_db)):
    homes = (
        db.query(Home)
        .filter(Home.active == True)
        .order_by(Home.section.asc(), Home.sort_order.asc(), Home.id.asc())
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