from fastapi import APIRouter, Depends, HTTPException, Request, Query
from sqlalchemy.orm import Session
from typing import Annotated

from api.db.database import get_db
from api.db.models import Node, Route, User
from api.schemas.routes import NodeRead, RouteRead, RouteCreate, RouteUpdate
from api.core.auth import get_current_user
import api.services.graph as graph

'''
    Not to be confused with Route.py
    Returns the predetermined routes
'''

router = APIRouter()

@router.post("", response_model=RouteRead)
def create_route(data: RouteCreate, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    route = Route(
        name=data.name,
        description=data.description,
        image_url=data.image_url,
        node_ids=[],
        stops=[],
        creator_employee_id=current_user.id,
        updated_employee_id=current_user.id,
    )
    db.add(route)
    db.commit()
    db.refresh(route)
    return route

# Routes
@router.get("", response_model=list[RouteRead])
def get_routes(request: Request, db: Session = Depends(get_db)):
    routes = db.query(Route).all()
    base = str(request.base_url).rstrip("/")

    for r in routes:
        if r.image_url and not r.image_url.startswith("http"):
            r.image_url = base + r.image_url 
    return routes

@router.get("/admin", response_model=list[RouteRead])
def get_routes_admin(db: Session = Depends(get_db)):
    return db.query(Route).all()

# Location / Node queries
@router.get("/locations", response_model= list[NodeRead])
def get_nodes(db: Session = Depends(get_db)):
    return db.query(Node).all()

@router.put("/{route_id}", response_model=RouteRead)
def update_route(route_id: int, data: RouteUpdate, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    route = db.query(Route).filter(Route.id == route_id).first()
    if route is None:
        raise HTTPException(status_code=404, detail="Route could not be found.")
    for key, value in data.model_dump(exclude_unset=True).items():
        setattr(route, key, value)
    route.updated_employee_id = current_user.id
    db.commit()
    db.refresh(route)
    return route