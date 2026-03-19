from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from api.db.database import get_db
from api.db.models import Changes
from api.schemas.changes import ChangeRead
from api.core.auth import get_current_user

router = APIRouter(
    dependencies=[Depends(get_current_user)]
)

@router.get("", response_model=list[ChangeRead])
def get_routes(db: Session = Depends(get_db)):
    changes = db.query(Changes).order_by(Changes.timestamp.desc()).all()

    return changes