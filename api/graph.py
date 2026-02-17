import networkx as nx
from math import inf
from sqlalchemy.orm import Session

def buildGraph(nodes, edges):
    # Where nodes is a list ints (ids from the db)
    # Where edges is a list of triples (node, connected node, weight)

    graph = nx.Graph()

    graph.add_nodes_from(nodes)
    graph.add_weighted_edges_from(edges)

    # for name, weight in nodes:
    #     graph.nodes[name]["weight"] = weight

    return graph


def getRoute(graph, current, nodes):
    # Where graph is the current graph
    # Where nodes is a list of nodes to visit
    # Where closest is the closest node to the user

    remainingNodes = set(nodes)
    remainingNodes.discard(current)

    route = [current]
    #cost = 0
    position = current
    # Run every x mins.
    # Trigger every time u reach desired
    # Adaptive algorithm for repeating update graph
    while remainingNodes:
        distance, paths = nx.single_source_dijkstra(graph, position, weight="weight")
        nextTarget = None
        bestDistance = inf

        for node in remainingNodes:
            if node in distance and distance[node] < bestDistance:
                bestDistance = distance[node]
                nextTarget = node
    
        innerPath = paths[nextTarget]
        route.extend(innerPath[1:])

        remainingNodes.remove(nextTarget)
        position = nextTarget

    return route