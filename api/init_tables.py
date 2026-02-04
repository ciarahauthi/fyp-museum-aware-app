from database import engine, Base, SessionLocal
import models
from models import *

def init():
    Base.metadata.drop_all(bind=engine)
    Base.metadata.create_all(bind=engine)
    print("Tables created")

    with SessionLocal() as db:
        # Dummy Graph
        nodes = ["A", "B", "C", "D", "E", "F", "G"]
        edges = [("A", "B", 3),
                ("A", "C", 6),

                ("B", "C", 2),
                ("B", "D", 4),

                ("C", "F", 7),

                ("D", "E", 1),
                ("D", "F", 2),

                ("E", "G", 6),

                ("F", "G", 5),
                ]
        
        coords = {
            "A": (0.12, 0.37),
            "B": (0.20, 0.28),
            "C": (0.20, 0.46),
            "D": (0.40, 0.38),
            "E": (0.62, 0.27),
            "F": (0.56, 0.60),
            "G": (0.70, 0.37),
        }

        nodeObj = {}

        for name in nodes:
            node = Node(
                name=name,
                x = coords[name][0],
                y = coords[name][1]
            )
            db.add(node)
            db.flush()
            nodeObj[node.name] = node.id

        db.commit()
        for node, connNode, weight in edges:
            db.add(
                Edge(
                    node_id = nodeObj[node],
                    connected_node_id = nodeObj[connNode],
                    weight = weight,
                )
            )
        db.commit()

        user = User(first_name="Ciara", surname="Duffy", email="ciara@gmail.com")
        db.add(user)
        db.commit()
    
        # Dummy Beacons
        beacon = Beacon(
            name="Test beacon",
            description="Test beacon - the green one",
            uuid="B9407F30-F5F8-466E-AFF9-25556B57FE6D",
            major=1,
            minor=4,
            employee_id=user.id,
            location_id = nodeObj["A"]
            )
        db.add(beacon)
        db.commit()

        beacon2 = Beacon(
            name="Test beacon 2",
            description="Test beacon 2 - the white one",
            uuid="B9407F30-F5F8-466E-AFF9-25556B57FE6D",
            major=1,
            minor=3,
            employee_id=user.id,
            location_id = nodeObj["B"]
            )
        db.add(beacon2)
        db.commit()

        beacon3 = Beacon(
            name="Test beacon 3",
            description="Test beacon 3 - the navy one",
            uuid="B9407F30-F5F8-466E-AFF9-25556B57FE6D",
            major=1,
            minor=2,
            employee_id=user.id,
            location_id = nodeObj["C"]
            )
        db.add(beacon3)
        db.commit()

        beacon4 = Beacon(
            name="Test beacon 4",
            description="Test beacon 4 - the light blue one",
            uuid="B9407F30-F5F8-466E-AFF9-25556B57FE6D",
            major=1,
            minor=1,
            employee_id=user.id,
            location_id = nodeObj["D"]
            )
        db.add(beacon4)
        db.commit()

        # Dummy Categories
        category = Category(
            name="Animal", 
            description="Animal category",
            employee_id=user.id)
        db.add(category)
        db.commit()

        # Dummy Exhibits
        exhibit = Exhibit(
            title="Rusty",
            description="Cute dog.",
            beacon_id=beacon.id,
            category_id=category.id,
            employee_id=user.id,
            child_friendly=True,
            likes=0,
            dislikes=0
            )
        db.add(exhibit)
        db.commit()

        exhibit1 = Exhibit(
            title="Marmalade",
            description="Cute cat.",
            beacon_id=beacon2.id,
            category_id=category.id,
            employee_id=user.id,
            child_friendly=True,
            likes=0,
            dislikes=0
            )
        db.add(exhibit1)
        db.commit()

        exhibit2 = Exhibit(
            title="Speckles",
            description="Cute chicken.",
            beacon_id=beacon3.id,
            category_id=category.id,
            employee_id=user.id,
            child_friendly=True,
            likes=0,
            dislikes=0
            )
        db.add(exhibit2)
        db.commit()

        exhibit3 = Exhibit(
            title="Eeyore",
            description="Cute donkey.",
            beacon_id=beacon4.id,
            category_id=category.id,
            employee_id=user.id,
            child_friendly=True,
            likes=0,
            dislikes=0
            )
        db.add(exhibit3)
        db.commit()
    print("Added dummy data")

if __name__ == "__main__":
    init()