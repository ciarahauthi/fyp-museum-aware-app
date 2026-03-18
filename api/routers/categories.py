from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from api.db.database import get_db
from api.db.models import Category, User, ChangeType
from api.schemas.categories import CategoryRead, CategoryCreate, CategoryUpdate
from api.core.auth import get_current_user
from api.core.changelog import log_change

router = APIRouter()

@router.post("", response_model=CategoryRead)
def create_category(data: CategoryCreate, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    category = Category(
        name=data.name,
        description=data.description,
        creator_employee_id=current_user.id,
        updated_employee_id=current_user.id,
    )
    db.add(category)
    db.commit()
    db.refresh(category)
    log_change(db, ChangeType.CREATE, "category", {"id": category.id, "name": category.name, "description" : category.description}, current_user.id)
    return category

@router.get("", response_model= list[CategoryRead])
def get_categories(db: Session = Depends(get_db)):
    return db.query(Category).all()

@router.get("/{category_id}", response_model= CategoryRead)
def get_category(category_id: int, db: Session = Depends(get_db)):
    category = db.query(Category).filter(Category.id == category_id).first()
    if category is None:
        raise HTTPException(status_code=404, detail="Category could not be found.")
    return category

@router.put("/{category_id}", response_model=CategoryRead)
def update_category(category_id: int, data: CategoryUpdate, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    category = db.query(Category).filter(Category.id == category_id).first()
    if category is None:
        raise HTTPException(status_code=404, detail="Category could not be found.")
    for key, value in data.model_dump(exclude_unset=True).items():
        setattr(category, key, value)
    category.updated_employee_id = current_user.id
    db.commit()
    db.refresh(category)
    log_change(db, ChangeType.UPDATE, "category", {"id": category.id, "name": category.name, "changes": data.model_dump(exclude_unset=True)}, current_user.id)
    return category
