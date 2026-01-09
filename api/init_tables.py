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
            description="Test beacon",
            uuid="B9407F30-F5F8-466E-AFF9-25556B57FE6D",
            major=1,
            minor=4,
            employee_id=user.id)
        db.add(beacon)
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
            dislikes=0)

        db.add(exhibit)
        db.commit()
    print("Added dummy data")

if __name__ == "__main__":
    init()