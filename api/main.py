from fastapi import FastAPI
from fastapi.staticfiles import StaticFiles
from fastapi.middleware.cors import CORSMiddleware
from api.core.lifespan import lifespan
from api.routers import auth, users, beacons, exhibits, categories, route, routes, home, beacon_events, images, changes
from api.core.settings import get_settings

settings = get_settings()
app = FastAPI(lifespan=lifespan)
app.mount("/api/static", StaticFiles(directory="api/static"), name="static")

app.add_middleware(
    CORSMiddleware,
    allow_origins=[settings.frontendUrl],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

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
app.include_router(images.router, prefix="/api/images", tags=["images"])
app.include_router(changes.router, prefix="/api/changes", tags=["changes"])