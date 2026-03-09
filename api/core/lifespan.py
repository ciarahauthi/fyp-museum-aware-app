from fastapi import FastAPI
from contextlib import asynccontextmanager
from api.db.database import SessionLocal
from sqlalchemy.orm import Session
from api.services.graph import getGraphFromDb

@asynccontextmanager
async def lifespan(app: FastAPI):
    db: Session = SessionLocal()
    try:
        app.state.graph = getGraphFromDb(db)
        yield
    finally:
        db.close()
        app.state.graph = None