from fastapi import APIRouter, Depends, HTTPException, Request
from sqlalchemy.orm import Session

from api.db.database import get_db
from api.db.models import Node, Route, User, ChangeType
from api.schemas.routes import NodeRead, RouteRead, RouteReadAdmin, RouteCreate, RouteUpdate
from api.core.auth import get_current_user
from api.db.models import Exhibit
from api.core.changelog import log_change

'''
    Not to be confused with Route.py
    Returns the predetermined routes
'''

router = APIRouter()

def derive_node_ids(exhibit_ids: list[int], db: Session) -> list[int]:
    exhibits = db.query(Exhibit).filter(Exhibit.id.in_(exhibit_ids)).all()
    seen = set()
    node_ids = []
    for e in exhibits:
        loc = e.location
        if loc is not None and loc not in seen:
            seen.add(loc)
            node_ids.append(loc)
    return node_ids

@router.post("", response_model=RouteReadAdmin)
def create_route(data: RouteCreate, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    route = Route(
        name=data.name,
        description=data.description,
        active=data.active,
        image_url=data.image_url,
        node_ids=derive_node_ids(data.stops, db),
        stops=data.stops,
        creator_employee_id=current_user.id,
        updated_employee_id=current_user.id,
    )
    db.add(route)
    db.commit()
    db.refresh(route)
    log_change(db, ChangeType.CREATE, "route", {"id": route.id, "name": route.name, "description": route.description}, current_user.id)
    return route

# Routes
@router.get("", response_model=list[RouteRead])
def get_routes(request: Request, db: Session = Depends(get_db)):
    routes = db.query(Route).filter(Route.active == True).all()
    base = str(request.base_url).rstrip("/")

    for r in routes:
        if r.image_url and not r.image_url.startswith("http"):
            r.image_url = base + r.image_url 
    return routes

@router.get("/admin", response_model=list[RouteReadAdmin])
def get_routes_admin(db: Session = Depends(get_db)):
    return db.query(Route).all()

# Location / Node queries
@router.get("/locations", response_model= list[NodeRead])
def get_nodes(db: Session = Depends(get_db)):
    return db.query(Node).all()

@router.put("/{route_id}", response_model=RouteReadAdmin)
def update_route(route_id: int, data: RouteUpdate, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    route = db.query(Route).filter(Route.id == route_id).first()
    if route is None:
        raise HTTPException(status_code=404, detail="Route could not be found.")
    for key, value in data.model_dump(exclude_unset=True).items():
        setattr(route, key, value)
    if data.stops is not None:
        route.node_ids = derive_node_ids(data.stops, db)
    route.updated_employee_id = current_user.id
    db.commit()
    db.refresh(route)
    log_change(db, ChangeType.UPDATE, "route", {"id": route.id, "name": route.name, "changes": data.model_dump(exclude_unset=True)}, current_user.id)
    return route