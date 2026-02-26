from fastapi import FastAPI, Depends, HTTPException, Query, Request, Response, status
from sqlalchemy.orm import Session
from sqlalchemy import tuple_
from database import get_db, SessionLocal
from models import *
from schemas import *
from typing import Annotated
import graph
from contextlib import asynccontextmanager
from fastapi.staticfiles import StaticFiles

@asynccontextmanager
async def lifespan(app: FastAPI):
    db: Session = SessionLocal()

    try:
        app.state.graph = getGraphFromDb(db)

    finally:
        db.close()

    yield 

    # Clear graph on shutdown
    app.state.graph = None

app = FastAPI(lifespan=lifespan)
app.mount("/api/static", StaticFiles(directory="static"), name="static")


# GRAPH
def getGraphFromDb(db: Session):
    node_rows = db.query(Node.id).all()
    edge_rows = db.query(Edge.node_id, Edge.connected_node_id, Edge.weight).all()

    node_ids = [r[0] for r in node_rows]
    edge_list = [(e[0], e[1], e[2]) for e in edge_rows]

    return graph.buildGraph(nodes=node_ids, edges=edge_list)


def getGraph(request: Request):
    graph = request.app.state.graph
    if graph is None:
        raise HTTPException(status_code=503, detail="Graph not loaded")
    return graph


# @app.get("/api/graph/test")
# def test_graph(db: Session = Depends(get_db)):
#     getGraphFromDb(db)

# @app.get("/api/route/test")
# def test_graph_route(request: Request):
#     g = getGraph(request)

#     return(graph.getRoute(g, 7, [5, 1, 3])) # G, [E, A, C]

# API ROUTES
@app.get("/")
async def root():
    return {"message": "Hello World"}

# User queries
@app.post("/api/users/", response_model=UserRead)
def create_user(
    data: UserCreate,
    db: Session = Depends(get_db)
):
    # Check if user already exists
    existingUser = db.query(User).filter(User.email == data.email).first()
    if existingUser:
        raise HTTPException(status_code=400, detail="Email already in use")
    
    user = User(
        first_name = data.first_name, 
        surname = data.surname, 
        email = data.email
    )
    
    db.add(user)
    db.commit()
    db.refresh(user)
    return user

@app.get("/api/users/", response_model= list[UserRead])
def get_users(db: Session = Depends(get_db)):
    return db.query(User).all()

@app.get("/api/users/{user_id}", response_model= UserRead)
def get_user(user_id: int, db: Session = Depends(get_db)):
    user = db.query(User).filter(User.id == user_id).first()

    if user is None:
        raise HTTPException(status_code=404, detail="User could not be found.")
    return user

# Beacon Queries
@app.get("/api/beacons/", response_model= list[BeaconRead])
def get_beacons(db: Session = Depends(get_db)):
    return db.query(Beacon).all()

@app.get("/api/beacons/{beacon_id}", response_model= BeaconRead)
def get_beacon(beacon_id: int, db: Session = Depends(get_db)):
    beacon = db.query(Beacon).filter(Beacon.id == beacon_id).first()

    if beacon is None:
        raise HTTPException(status_code=404, detail="Beacon could not be found.")
    return beacon

# Exhibit Queries
@app.get("/api/exhibits/", response_model=list[ExhibitReadPublic])
def get_exhibits(request: Request, db: Session = Depends(get_db)):
    exhibits = db.query(Exhibit).all()
    base = str(request.base_url).rstrip("/")

    for e in exhibits:
        if e.image_url and not e.image_url.startswith("http"):
            e.image_url = base + e.image_url
    return exhibits

@app.get("/api/exhibits/lookup", response_model= ExhibitRead)
def get_exhibit_by_beacon(beacon_uuid: str, beacon_major: int, beacon_minor: int, db: Session = Depends(get_db)):
    exhibit = db.query(Exhibit).join(Beacon, Exhibit.beacon_id == Beacon.id).filter(Beacon.uuid == beacon_uuid, Beacon.major == beacon_major, Beacon.minor == beacon_minor).first()

    if exhibit is None:
        raise HTTPException(status_code=404, detail="Exhibit could not be found.")
    return exhibit

@app.get("/api/exhibits/lookup_many", response_model= list[ExhibitReadPublic])
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

@app.get("/api/exhibits/{exhibit_id}", response_model= ExhibitRead)
def get_exhibit(exhibit_id: int, db: Session = Depends(get_db)):
    exhibit = db.query(Exhibit).filter(Exhibit.id == exhibit_id).first()

    if exhibit is None:
        raise HTTPException(status_code=404, detail="Exhibit could not be found.")
    return exhibit

# Category Queries
@app.get("/api/categories/", response_model= list[CategoryRead])
def get_categories(db: Session = Depends(get_db)):
    return db.query(Category).all()

@app.get("/api/categories/{category_id}", response_model= CategoryRead)
def get_category(category_id: int, db: Session = Depends(get_db)):
    category = db.query(Category).filter(Category.id == category_id).first()

    if category is None:
        raise HTTPException(status_code=404, detail="Category could not be found.")
    return category

# Location / Node queries
@app.get("/api/locations/", response_model= list[NodeRead])
def get_nodes(db: Session = Depends(get_db)):
    return db.query(Node).all()

# Routes
@app.get("/api/routes/", response_model=list[RouteRead])
def get_routes(request: Request, db: Session = Depends(get_db)):
    routes = db.query(Route).all()
    base = str(request.base_url).rstrip("/")

    for r in routes:
        if r.image_url and not r.image_url.startswith("http"):
            r.image_url = base + r.image_url 
    return routes

@app.get("/api/route", response_model=list[int])
def get_route(
    request: Request,
    current: int,
    targets: Annotated[list[int], Query(...)],
):
    g = getGraph(request)

    if current not in g:
        raise HTTPException(status_code=400, detail=f"current node {current} not in graph")
    missing = [t for t in targets if t not in g]
    if missing:
        raise HTTPException(status_code=400, detail=f"target nodes not in graph: {missing}")

    return graph.getRoute(g, current, targets)

# Rating feature
@app.post("/api/rate", status_code=status.HTTP_204_NO_CONTENT)
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