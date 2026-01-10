from database import engine, Base, SessionLocal
import models
from models import *

def init():
    Base.metadata.drop_all(bind=engine)
    Base.metadata.create_all(bind=engine)
    print("Tables created")

    with SessionLocal() as db:
        user = User(first_name="Ciara", surname="Duffy", email="ciara@gmail.com")
        db.add(user)
        db.commit()

        beacon = Beacon(
            name="Test beacon",
            description="Test beacon - the green one",
            uuid="B9407F30-F5F8-466E-AFF9-25556B57FE6D",
            major=1,
            minor=4,
            employee_id=user.id)
        db.add(beacon)
        db.commit()

        beacon2 = Beacon(
            name="Test beacon 2",
            description="Test beacon 2 - the white one",
            uuid="B9407F30-F5F8-466E-AFF9-25556B57FE6D",
            major=1,
            minor=3,
            employee_id=user.id)
        db.add(beacon2)
        db.commit()

        beacon3 = Beacon(
            name="Test beacon 3",
            description="Test beacon 3 - the navy one",
            uuid="B9407F30-F5F8-466E-AFF9-25556B57FE6D",
            major=1,
            minor=2,
            employee_id=user.id)
        db.add(beacon3)
        db.commit()

        beacon4 = Beacon(
            name="Test beacon 4",
            description="Test beacon 4 - the light blue one",
            uuid="B9407F30-F5F8-466E-AFF9-25556B57FE6D",
            major=1,
            minor=1,
            employee_id=user.id)
        db.add(beacon4)
        db.commit()

        category = Category(
            name="Animal", 
            description="Animal category",
            employee_id=user.id)
        db.add(category)
        db.commit()

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