from fastapi import APIRouter, Depends, HTTPException, Request, Query, Request
from sqlalchemy.orm import Session
from typing import Annotated

import api.services.graph as graph

'''
    Not to be confused with Routes.py
    Returns the custom route the user requested
'''

router = APIRouter()

def getGraph(request: Request):
    graph = request.app.state.graph
    if graph is None:
        raise HTTPException(status_code=503, detail="Graph not loaded")
    return graph

@router.get("", response_model=list[int])
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