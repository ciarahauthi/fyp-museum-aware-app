from database import engine, Base, SessionLocal
import models
from models import *

def init():
    Base.metadata.drop_all(bind=engine)
    Base.metadata.create_all(bind=engine)
    print("Tables created")

    with SessionLocal() as db:
        db.add(User(first_name="Ciara", surname="Duffy", email="ciara@gmail.com"))
        db.add(User(first_name="Test", surname="Test", email="test@gmail.com"))
        db.commit()

    print("Added dummy data")

if __name__ == "__main__":
    init()