from database import engine, Base, SessionLocal
from models import *
from auth import hash_password

def init():
    Base.metadata.drop_all(bind=engine)
    Base.metadata.create_all(bind=engine)
    print("Tables created")

    with SessionLocal() as db:
        user = User(
            first_name="Ciara", 
            surname="Duffy", 
            email="ciaraduffy24@gmail.com",
            password_hash=hash_password("Password.123")
            )
        db.add(user)
        db.commit()

        # Dummy Graph
        nodes = ["A", "B", "C", "D", "E", "F", "G"]
        edges = [
            ("A", "B", 3),
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
                    node_id=nodeObj[node],
                    connected_node_id=nodeObj[connNode],
                    weight=weight,
                )
            )
        db.commit()

        # Dummy Routes
        db.add_all([
            Route(
                name="Route 1",
                description="G -> E -> D -> B -> C -> B -> A",
                node_ids =[7, 5, 4, 2, 3, 2, 1],
                stops = [1, 2, 3, 4],
                creator_employee_id=user.id,
                updated_employee_id=user.id
            ),
            Route(
                name="Route 2",
                description="A -> B -> D -> F -> G",
                node_ids=[1, 2, 4, 6, 7],
                stops = [1, 2],
                creator_employee_id=user.id,
                updated_employee_id=user.id
            ),
            Route(
                name="Route 3",
                description="C -> B -> D -> E -> G",
                node_ids=[3, 2, 4, 5, 7],
                stops = [3, 4],
                creator_employee_id=user.id,
                updated_employee_id=user.id
            ),
            Route(
                name="Route 4",
                description="A -> C -> F -> G",
                node_ids=[1, 3, 6, 7],
                stops = [1, 3, 4],
                creator_employee_id=user.id,
                updated_employee_id=user.id
            ),
        ])
        db.commit()
    
        # Dummy Beacons
        beacon = Beacon(
            name="Test beacon",
            description="Test beacon - the green one",
            uuid="B9407F30-F5F8-466E-AFF9-25556B57FE6D",
            major=1,
            minor=4,
            creator_employee_id=user.id,
            updated_employee_id=user.id,
            location_id=nodeObj["A"]
        )
        db.add(beacon)
        db.commit()

        beacon2 = Beacon(
            name="Test beacon 2",
            description="Test beacon 2 - the white one",
            uuid="B9407F30-F5F8-466E-AFF9-25556B57FE6D",
            major=1,
            minor=3,
            creator_employee_id=user.id,
            updated_employee_id=user.id,
            location_id=nodeObj["B"]
        )
        db.add(beacon2)
        db.commit()

        beacon3 = Beacon(
            name="Test beacon 3",
            description="Test beacon 3 - the navy one",
            uuid="B9407F30-F5F8-466E-AFF9-25556B57FE6D",
            major=1,
            minor=2,
            creator_employee_id=user.id,
            updated_employee_id=user.id,
            location_id=nodeObj["C"]
        )
        db.add(beacon3)
        db.commit()

        beacon4 = Beacon(
            name="Test beacon 4",
            description="Test beacon 4 - the light blue one",
            uuid="B9407F30-F5F8-466E-AFF9-25556B57FE6D",
            major=1,
            minor=1,
            creator_employee_id=user.id,
            updated_employee_id=user.id,
            location_id=nodeObj["D"]
        )
        db.add(beacon4)
        db.commit()

        # Dummy Categories
        category = Category(
            name="Animal",
            description="Animal category",
            creator_employee_id=user.id,
            updated_employee_id=user.id
        )
        db.add(category)
        db.commit()

        paintCategory = Category(
            name="Painting",
            description="Paintings category",
            creator_employee_id=user.id,
            updated_employee_id=user.id
        )
        db.add(paintCategory)
        db.commit()

        sculptureCategory = Category(
            name="Sculpture",
            description="Sculptures Category",
            creator_employee_id=user.id,
            updated_employee_id=user.id
        )
        db.add(sculptureCategory)
        db.commit()

        # Dummy Exhibits
        exhibit = Exhibit(
            title="David",
            description=(
                "David is a masterpiece of Italian Renaissance sculpture in marble created from 1501 to 1504 by Michelangelo.\n\n"
                "With a height of 5.17 metres (17 ft 0 in), the David was not only the first colossal marble statue made in the High Renaissance, "
                "but also the first since classical antiquity, setting a precedent for the 16th century and beyond. David was originally commissioned "
                "as one of a series of statues of twelve prophets to be positioned along the roofline of the east end of Florence Cathedral, but was "
                "instead placed in the public square in front of the Palazzo della Signoria, the seat of civic government in Florence, where it was "
                "unveiled on 8 September 1504.\n\n"
                "In 1873, the statue was moved to the Galleria dell'Accademia, Florence. In 1910 a replica was installed at the original site on the public square."
            ),
            beacon_id=beacon.id,
            category_id=sculptureCategory.id,
            creator_employee_id=user.id,
            updated_employee_id=user.id,
            child_friendly=True,
            likes=100,
            dislikes=0,
            image_url="/api/static/exhibits/david.jpg"
        )
        db.add(exhibit)
        db.commit()

        exhibit1 = Exhibit(
            title="Monet's Water Lilies",
            description=(
                "Water Lilies is a series of approximately 250 oil paintings by French Impressionist Claude Monet (1840–1926).\n\n"
                "The paintings depict his flower garden at his home in Giverny, and were the main focus of his artistic production during the last 31 years of his life. "
                "Many of the works were painted while Monet suffered from cataracts."
            ),
            beacon_id=beacon2.id,
            category_id=paintCategory.id,
            creator_employee_id=user.id,
            updated_employee_id=user.id,
            child_friendly=True,
            likes=50,
            dislikes=2,
            image_url="/api/static/exhibits/monet_water_lilies.jpg"
        )
        db.add(exhibit1)
        db.commit()

        exhibit2 = Exhibit(
            title="Venus de Milo",
            description=(
                "The Venus de Milo or Aphrodite of Melos is an ancient Greek marble sculpture that was created during the Hellenistic period.\n\n"
                "Its exact dating is uncertain, but the modern consensus places it in the 2nd century BC, perhaps between 160 and 110 BC. It was discovered in 1820 "
                "on the island of Milos, Greece, and has been displayed at the Louvre Museum since 1821.\n\n"
                "The Venus de Milo is believed to depict Aphrodite, the Greek goddess of love, whose Roman counterpart was Venus. Made of Parian marble, the statue is "
                "larger than life size, standing over 2 metres (6 ft 7 in) high. The statue is missing both arms."
            ),
            beacon_id=beacon3.id,
            category_id=sculptureCategory.id,
            creator_employee_id=user.id,
            updated_employee_id=user.id,
            child_friendly=True,
            likes=50,
            dislikes=10,
            image_url="/api/static/exhibits/venus_de_milo.jpg"
        )
        db.add(exhibit2)
        db.commit()

        exhibit3 = Exhibit(
            title="Mona Lisa",
            description=(
                "The Mona Lisa is a half-length portrait painting by the Italian artist Leonardo da Vinci.\n\n"
                "Considered an archetypal masterpiece of the Italian Renaissance, it has been described as 'the best known, the most visited, the most written about, "
                "the most sung about, [and] the most parodied work of art in the world.'\n\n"
                "The painting has been traditionally considered to depict the Italian noblewoman Lisa del Giocondo."
            ),
            beacon_id=beacon4.id,
            category_id=paintCategory.id,
            creator_employee_id=user.id,
            updated_employee_id=user.id,
            child_friendly=True,
            likes=0,
            dislikes=0,
            image_url="/api/static/exhibits/mona_lisa.jpg"
            )
        db.add(exhibit3)
        db.commit()

        # Dummy Home content (for Open Day demo)
        db.add_all([
            Home(
                section="top",
                sort_order=0,
                active=True,
                title="Computer Science Open Day 2026",
                description="Welcome to our Computer Science Open Day! We hope you have a great time seeing all the projects every student has made.",
                creator_employee_id=user.id,
                updated_employee_id=user.id
            ),
            Home(
                section="mid",
                sort_order=0,
                active=True,
                title="How This App Works",
                description=(
                    "The Location-Aware Museum App uses Bluetooth Low Energy (BLE) beacons to detect your approximate position inside the building.\n\n"
                    "When it detects beacons nearby, it will derive your location based off of information from the BLE packets."
                    "When you request to route, it sends a request to the server which responds with the shortest path from yur current location to all desired objects.\n\n"
                    "Visitors can read up on museum news and info on the home screen, browse available exhibits, try pre-curated and personalised routes, and leave feedback."
                ),
                creator_employee_id=user.id,
                updated_employee_id=user.id
            ),
            Home(
                section="bottom",
                sort_order=0,
                active=True,
                title="About This Project",
                description=(
                    "This project was developed as a Final Year Computer Science project to improve museum visitor experience using indoor positioning and smart routing within a mobile app."
                    "It is also is an investigation into how Bluetooth LE can be used as a low-cost and accurate way of deriving indoor positioning!\n\n"
                    "The app is made up of the following:\n"
                    "Android: Kotlin and Jetpack Compose. RoomDB, Koin, Coil and Retrofit.\n"
                    "Backend: Python, SQL. Using FastAPI + SQLAlchemy.\n"
                    "Routing: NetworkX with a graph-based shortest path (A modified version of Dijkstra's Algorithm)\n\n"
                    "Thanks for checking out the demo and I hope you enjoy testing it out!"
                ),
                creator_employee_id=user.id,
                updated_employee_id=user.id
            ),
            Home(
                section="bottom",
                sort_order=1,
                active=True,
                title="About Me",
                description=(
                    "Hi! I’m Ciara. I'm a 4th year computer science student. I built this as a full-stack mobile project.\n\n"
                    "Following my internship I found my passion in tech was Mobile Development- specifically Android development!"
                ),
                creator_employee_id=user.id,
                updated_employee_id=user.id
            ),
        ])
        db.commit()

    print("Added dummy data")

if __name__ == "__main__":
    init()