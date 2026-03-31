from fastapi import APIRouter, HTTPException, Request, Query
from typing import Annotated

from api.schemas.routes import RouteResponse
import api.services.graph as graph_service

'''
    Not to be confused with Routes.py
    Returns the custom route the user requested
'''

router = APIRouter()

MINUTES_PER_EXHIBIT = 5

def getGraph(request: Request):
    g = request.app.state.graph
    if g is None:
        raise HTTPException(status_code=503, detail="Graph not loaded")
    return g

'''
5 mins per exhibit added on to route cost
'''
@router.get("", response_model=RouteResponse)
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

    path = graph_service.getRoute(g, current, targets)
    cost = graph_service.routeDistance(g, path)
    estimated_minutes = round(cost + len(targets) * MINUTES_PER_EXHIBIT)

    return RouteResponse(path=path, estimated_minutes=estimated_minutes)
