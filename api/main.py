from fastapi import FastAPI
from fastapi.staticfiles import StaticFiles
from api.core.lifespan import lifespan
from api.routers import auth, users, beacons, exhibits, categories, route, routes, home, beacon_events

app = FastAPI(lifespan=lifespan)
app.mount("/api/static", StaticFiles(directory="api/static"), name="static")

# API ROUTES
@app.get("/")
async def root():
    return {"message": "Hello World"}

app.include_router(auth.router, prefix="/api/auth", tags=["auth"])
app.include_router(users.router, prefix="/api/users", tags=["users"])
app.include_router(beacons.router, prefix="/api/beacons", tags=["beacons"])
app.include_router(exhibits.router, prefix="/api/exhibits", tags=["exhibits"])
app.include_router(categories.router, prefix="/api/categories", tags=["categories"])
app.include_router(route.router, prefix="/api/route", tags=["route"]) # User specific route
app.include_router(routes.router, prefix="/api/routes", tags=["routes"]) # general routes and locations
app.include_router(home.router, prefix="/api/home", tags=["home"])
app.include_router(beacon_events.router, prefix="/api/beacon_events", tags=["beacon-events"])