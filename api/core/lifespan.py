import asyncio
from fastapi import FastAPI
from contextlib import asynccontextmanager
from api.db.database import SessionLocal
from api.services.graph import getGraphFromDb, applyWeights

async def refresh_graph_loop(app: FastAPI):
    # Rebuild graph with congestion weights every hour
    while True:
        await asyncio.sleep(3600)
        try:
            db = SessionLocal()
            try:
                base = getGraphFromDb(db)
                app.state.graph = applyWeights(db, base)
                print("Graph weights refreshed")
            finally:
                db.close()
        except Exception as e:
            print(f"Graph weight refresh failed: {e}")

@asynccontextmanager
async def lifespan(app: FastAPI):
    db = SessionLocal()
    try:
        base = getGraphFromDb(db)
        app.state.graph = applyWeights(db, base)
    finally:
        db.close()

    task = asyncio.create_task(refresh_graph_loop(app))
    try:
        yield
    finally:
        task.cancel()
        app.state.graph = None
