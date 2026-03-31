import networkx as nx
from math import inf
from datetime import datetime, timezone
from sqlalchemy import func
from sqlalchemy.orm import Session

from api.db.models import Node, Edge, BeaconEvents, Beacon
import api.services.graph as graph

# GRAPH
def getGraphFromDb(db: Session):
    node_rows = db.query(Node.id).all()
    edge_rows = db.query(Edge.node_id, Edge.connected_node_id, Edge.weight).all()

    node_ids = [r[0] for r in node_rows]
    edge_list = [(e[0], e[1], e[2]) for e in edge_rows]

    return graph.buildGraph(nodes=node_ids, edges=edge_list)


def buildGraph(nodes, edges):
    '''
    Where nodes is a list ints (ids from the db)
    Where edges is a list of triples (node, connected node, weight)
    '''

    graph = nx.Graph()

    graph.add_nodes_from(nodes)
    graph.add_weighted_edges_from(edges)

    # for name, weight in nodes:
    #     graph.nodes[name]["weight"] = weight

    return graph


MIN_SESSIONS = 10
MAX_MULTIPLIER = 2.5

def applyWeights(db: Session, base_graph: nx.Graph) -> nx.Graph:
    '''
    Returns a copy of base_graph with edge weights scaled by historical congestion for the current (day of week, hour).
    >= 10 distinct sessions to apply a multiplier.
    Max 2.5x multiplier value.
    '''
    now = datetime.now(timezone.utc)
    dayOfWeek = now.isoweekday() % 7 + 1
    hour = now.hour

    sessionsByNode = getCurrentSlotSessions(db, dayOfWeek, hour)
    if not any(count >= MIN_SESSIONS for count in sessionsByNode.values()):
        return base_graph

    avgSessionsByNode = getAvgSessionsForDay(db, dayOfWeek)
    multipliers = calcMultipliers(sessionsByNode, avgSessionsByNode)
    if not multipliers:
        return base_graph

    return applyMultipliersToGraph(base_graph, multipliers)


def getCurrentSlotSessions(db: Session, dayOfWeek: int, hour: int):

    # Distinct session counts per node for a given (day of week, hour)
    rows = (
        db.query(
            Beacon.location_id.label("nodeId"),
            func.count(func.distinct(BeaconEvents.session_id)).label("count")
        )
        .join(BeaconEvents, BeaconEvents.beacon_id == Beacon.id)
        .filter(
            func.dayofweek(BeaconEvents.recorded_at) == dayOfWeek,
            func.hour(BeaconEvents.recorded_at) == hour
        )
        .group_by(Beacon.location_id)
        .all()
    )
    return {row.nodeId: row.count for row in rows}


def getAvgSessionsForDay(db: Session, dayOfWeek: int):

    # Average sessions per hour per node for a given day of week
    hourlyCounts = (
        db.query(
            Beacon.location_id.label("nodeId"),
            func.count(func.distinct(BeaconEvents.session_id)).label("sessionCount")
        )
        .join(BeaconEvents, BeaconEvents.beacon_id == Beacon.id)
        .filter(func.dayofweek(BeaconEvents.recorded_at) == dayOfWeek)
        .group_by(Beacon.location_id, func.hour(BeaconEvents.recorded_at))
        .subquery()
    )

    rows = (
        db.query(
            hourlyCounts.c.nodeId,
            func.avg(hourlyCounts.c.sessionCount).label("avgCount")
        )
        .group_by(hourlyCounts.c.nodeId)
        .all()
    )
    return {row.nodeId: float(row.avgCount) for row in rows}


def calcMultipliers(sessionsByNode: dict, avgByNode: dict) -> dict:
    '''
    Computes multiplier per node
    Average busyness = 1.0x. Scales up to 2.5x
    Quieter nodes stay at 1.0x
    '''
    multipliers = {}
    for nodeId, count in sessionsByNode.items():
        if count < MIN_SESSIONS:
            continue
        avg = avgByNode.get(nodeId, 1.0)
        if avg <= 0:
            continue
        ratio = count / avg
        multipliers[nodeId] = 1.0 + min(max(ratio - 1.0, 0.0), MAX_MULTIPLIER - 1.0)
    return multipliers


def applyMultipliersToGraph(base_graph: nx.Graph, multipliers: dict) -> nx.Graph:
    '''
    Scales edge weights using the busier of the two endpoint multipliers
    Works on a copy of base graph
    '''
    adjusted = base_graph.copy()
    for u, v, data in base_graph.edges(data=True):
        edgeMultiplier = max(multipliers.get(u, 1.0), multipliers.get(v, 1.0))
        adjusted[u][v]["weight"] = round(data.get("weight", 1) * edgeMultiplier, 2)
    return adjusted


def routeDistance(g: nx.Graph, path: list) -> float:

    # Sum of edge weights along consecutive nodes in a path
    total = 0.0
    for i in range(len(path) - 1):
        data = g.get_edge_data(path[i], path[i + 1])
        if data:
            total += data.get("weight", 0)
    return total

# Greedy ALgorithm

# def getRoute(graph, current, nodes):
#     '''
#     Where graph is the current graph
#     Where nodes is a list of nodes to visit
#     Where closest is the closest node to the user
#     '''

#     remainingNodes = set(nodes)
#     remainingNodes.discard(current)

#     route = [current]
#     #cost = 0
#     position = current
#     # Run every x mins.
#     # Trigger every time u reach desired
#     # Adaptive algorithm for repeating update graph
#     while remainingNodes:
#         distance, paths = nx.single_source_dijkstra(graph, position, weight="weight")
#         nextTarget = None
#         bestDistance = inf

#         for node in remainingNodes:
#             if node in distance and distance[node] < bestDistance:
#                 bestDistance = distance[node]
#                 nextTarget = node
    
#         innerPath = paths[nextTarget]
#         route.extend(innerPath[1:])

#         remainingNodes.remove(nextTarget)
#         position = nextTarget

#     return route


'''
    NN + 2OPT algorithm
    https://phabe.ch/2024/08/27/how-to-improve-tsp-tours-applying-the-2-opt-neighborhood/

    1. Precompute - Run dijkstras to make 
        dist: dist[A] = {S: 5, A: 0, B: 3, C: 7 ..... } - map. K = node, V = { K = another node, V = shortest distance value }
        paths: paths[A] = {S: [A, X, S], A: [A], B: [A, B], C: [A, X, Y, C] ..... } - K = Node, V = { K = another node, V = list of nodes to reach that node (shortest path)}

    2. Build an initial route using Nearest Neighbour

        Starting with current, take the nearest unvisited node given

    3. 2-opt - local search
        Local search - where good solutions are near / neighbours of other ones
        two_opt_swap is the neighbourhood definition
    
    4. 
        '''
def getRoute(graph, current, nodes):
    '''
    Where graph is the current graph
    Where nodes is a list of nodes to visit
    Where current is the closest node to the user
    '''
    required = [current] + [n for n in nodes if n != current]

    if not nodes:
        return [current]

    if len(required) == 1:
        return [current]

    # Precompute
    dist = {}
    paths = {}
    for node in required:
        d, p = nx.single_source_dijkstra(graph, node, weight="weight")
        dist[node] = d
        paths[node] = p

    # Construct initial ordering with NN
    remainingNodes = set(required)
    remainingNodes.discard(current)
    order = [current]
    position = current

    while remainingNodes:
        nextTarget = None
        bestDistance = inf

        for node in remainingNodes:
            if node in dist[position] and dist[position][node] < bestDistance:
                bestDistance = dist[position][node]
                nextTarget = node

        order.append(nextTarget)
        remainingNodes.remove(nextTarget)
        position = nextTarget

    # Improve with 2-opt
    best_order = two_opt_improve(order, dist)
    return best_order

def total_order_distance(order, dist):
    # Calculate total distance of an ordering using precomputed distances
    return sum(dist[order[i]][order[i+1]] for i in range(len(order) - 1))


def two_opt_swap(order, i, k):
    # Reverse the segment between i and k.
    return order[:i] + order[i:k+1][::-1] + order[k+1:]


def two_opt_improve(order, dist):
    # 2-opt local search: keep swapping until no improvement found
    best_order = order
    best_distance = total_order_distance(best_order, dist)
    improved = True

    while improved:
        improved = False
        for i in range(1, len(best_order) - 1):
            for k in range(i + 1, len(best_order)):
                new_order = two_opt_swap(best_order, i, k)
                new_distance = total_order_distance(new_order, dist)
                if new_distance < best_distance:
                    best_order = new_order
                    best_distance = new_distance
                    improved = True

    return best_order