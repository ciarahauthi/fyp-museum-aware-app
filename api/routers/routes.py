from fastapi import APIRouter, Depends, HTTPException, Request, Query, Request
from sqlalchemy.orm import Session
from typing import Annotated

from api.db.database import get_db
from api.db.models import Node, Route
from api.schemas.routes import NodeRead, RouteRead
import api.services.graph as graph

'''
    Not to be confused with Route.py
    Returns the general routes
'''

router = APIRouter()

# Routes
@router.get("", response_model=list[RouteRead])
def get_routes(request: Request, db: Session = Depends(get_db)):
    routes = db.query(Route).all()
    base = str(request.base_url).rstrip("/")

    for r in routes:
        if r.image_url and not r.image_url.startswith("http"):
            r.image_url = base + r.image_url 
    return routes

# Location / Node queries
@router.get("/locations", response_model= list[NodeRead])
def get_nodes(db: Session = Depends(get_db)):
    return db.query(Node).all()