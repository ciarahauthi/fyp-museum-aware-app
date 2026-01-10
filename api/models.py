from database import Base
from sqlalchemy import Column, Integer, String, Boolean, TIMESTAMP, text, ForeignKey
from sqlalchemy.orm import relationship

class User(Base):
    __tablename__ = "users"

    id = Column(Integer, primary_key=True, index=True)
    first_name = Column(String(100), nullable=False)
    surname = Column(String(100), nullable=False)
    email = Column(String(100), unique=True, index=True, nullable=False)

class Exhibit(Base):
    __tablename__ = "exhibit"

    id = Column(Integer, primary_key=True, index=True)
    title = Column(String(100), nullable=False)
    description = Column(String(2000), nullable=False)
    created_at = Column(TIMESTAMP(timezone=True), nullable=False, server_default=text('now()'))

    beacon_id = Column(Integer, ForeignKey("beacon.id", ondelete="SET NULL"), nullable=True)
    category_id = Column(Integer, ForeignKey("category.id"), nullable=False)
    employee_id = Column(Integer, ForeignKey("users.id"), nullable=False)

    child_friendly = Column(Boolean, nullable=False)
    likes = Column(Integer, nullable=False, server_default="0")
    dislikes = Column(Integer, nullable=False, server_default="0")

    category_obj = relationship("Category", lazy="joined")
    @property
    def category(self) -> str:
        return self.category_obj.name

class Category(Base):
    __tablename__ = "category"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String(100), nullable=False)
    description = Column(String(2000), nullable=False)
    created_at = Column(TIMESTAMP(timezone=True), nullable=False, server_default=text('now()'))

    employee_id = Column(Integer, ForeignKey("users.id"), nullable=False)

class Beacon(Base):
    __tablename__ = "beacon"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String(100), nullable=False)
    description = Column(String(2000), nullable=False)
    uuid = Column(String(100), nullable=False)
    major = Column(Integer, nullable=False, server_default="0")
    minor = Column(Integer, nullable=False, server_default="0")
    created_at = Column(TIMESTAMP(timezone=True), nullable=False, server_default=text('now()'))

    employee_id = Column(Integer, ForeignKey("users.id"), nullable=False)