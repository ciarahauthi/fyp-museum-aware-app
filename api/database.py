from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, declarative_base, Session
from settings import get_settings

settings = get_settings()
engine = create_engine(settings.dbUrl)

SessionLocal = sessionmaker(
    autocommit = False,
    autoflush = False,
    bind = engine
)

Base = declarative_base()

def get_db():
    db: Session = SessionLocal()
    try:
        yield db
    finally:
        db.close()

print("database.py loaded, engine =", engine)
