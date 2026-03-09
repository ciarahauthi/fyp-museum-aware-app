from api.db.database import Base
from sqlalchemy import Column, Integer, String, Boolean, TIMESTAMP, text, ForeignKey, Numeric, JSON, Enum, Text, Index
from sqlalchemy.orm import relationship
import enum

TS_DEFAULT = text("CURRENT_TIMESTAMP")
TS_ONUPDATE = text("CURRENT_TIMESTAMP")

class User(Base):
    __tablename__ = "users"

    id = Column(Integer, primary_key=True, index=True)
    first_name = Column(String(100), nullable=False)
    surname = Column(String(100), nullable=False)
    email = Column(String(100), unique=True, index=True, nullable=False)
    password_hash = Column(String(255), nullable=True)

class Exhibit(Base):
    __tablename__ = "exhibit"

    id = Column(Integer, primary_key=True, index=True)
    title = Column(String(100), nullable=False)
    description = Column(Text, nullable=False)

    created_at = Column(TIMESTAMP(timezone=True), nullable=False, server_default=TS_DEFAULT)
    updated_at = Column(TIMESTAMP(timezone=True), nullable=False, server_default=TS_DEFAULT, onupdate=TS_ONUPDATE)

    creator_employee_id = Column(Integer, ForeignKey("users.id"), nullable=False)
    updated_employee_id = Column(Integer, ForeignKey("users.id"), nullable=False)

    beacon_id = Column(Integer, ForeignKey("beacon.id", ondelete="SET NULL"), nullable=True)
    category_id = Column(Integer, ForeignKey("category.id"), nullable=False)

    child_friendly = Column(Boolean, nullable=False)
    likes = Column(Integer, nullable=False, server_default="0")
    dislikes = Column(Integer, nullable=False, server_default="0")
    image_url = Column(String(500), nullable=True)

    category_obj = relationship("Category", lazy="joined")
    beacon_obj = relationship("Beacon", lazy="joined")

    @property
    def uuid(self):
        return self.beacon_obj.uuid if self.beacon_obj else None

    @property
    def major(self):
        return self.beacon_obj.major if self.beacon_obj else None

    @property
    def minor(self):
        return self.beacon_obj.minor if self.beacon_obj else None

    @property
    def location(self):
        return self.beacon_obj.location_id if self.beacon_obj else None

    @property
    def category(self) -> str:
        return self.category_obj.name

class Category(Base):
    __tablename__ = "category"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String(100), nullable=False)
    description = Column(Text, nullable=False)

    created_at = Column(TIMESTAMP(timezone=True), nullable=False, server_default=TS_DEFAULT)
    updated_at = Column(TIMESTAMP(timezone=True), nullable=False, server_default=TS_DEFAULT, onupdate=TS_ONUPDATE)

    creator_employee_id = Column(Integer, ForeignKey("users.id"), nullable=False)
    updated_employee_id = Column(Integer, ForeignKey("users.id"), nullable=False)

class Beacon(Base):
    __tablename__ = "beacon"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String(100), nullable=False)
    description = Column(Text, nullable=False)
    uuid = Column(String(100), nullable=False)
    major = Column(Integer, nullable=False)
    minor = Column(Integer, nullable=False)

    created_at = Column(TIMESTAMP(timezone=True), nullable=False, server_default=TS_DEFAULT)
    updated_at = Column(TIMESTAMP(timezone=True), nullable=False, server_default=TS_DEFAULT, onupdate=TS_ONUPDATE)

    location_id = Column(Integer, ForeignKey("node.id"), nullable=False)
    creator_employee_id = Column(Integer, ForeignKey("users.id"), nullable=False)
    updated_employee_id = Column(Integer, ForeignKey("users.id"), nullable=False)

class Node(Base):
    __tablename__ = "node"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String(100), unique=True, nullable=False)
    x = Column(Numeric(3, 2), nullable=False)
    y = Column(Numeric(3, 2), nullable=False)

class Edge(Base):
    __tablename__ = "edges"

    id = Column(Integer, primary_key=True, index=True)
    node_id = Column(Integer, ForeignKey("node.id", ondelete="CASCADE"), nullable=False)
    connected_node_id = Column(Integer, ForeignKey("node.id", ondelete="CASCADE"), nullable=False)
    weight = Column(Integer, nullable=False, server_default="0")

    node_obj = relationship("Node", foreign_keys=[node_id], lazy="joined")
    connected_node_obj = relationship("Node", foreign_keys=[connected_node_id], lazy="joined")

class Route(Base):
    __tablename__ = "route"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String(100), unique=True, nullable=False)
    description = Column(Text, nullable=False)

    node_ids = Column(JSON, nullable=False)
    stops = Column(JSON, nullable=False)
    image_url = Column(String(500), nullable=True)

    creator_employee_id = Column(Integer, ForeignKey("users.id"), nullable=False)
    updated_employee_id = Column(Integer, ForeignKey("users.id"), nullable=False)

class HomeSection(str, enum.Enum):
    top = "top"
    mid = "mid"
    bottom = "bottom"

class Home(Base):
    __tablename__ = "home"

    id = Column(Integer, primary_key=True, index=True)
    section = Column(Enum(HomeSection), nullable=False)
    sort_order = Column(Integer, nullable=False, server_default="0")
    active = Column(Boolean, nullable=False, server_default="1")
    title = Column(String(150), nullable=False)
    description = Column(Text, nullable=False)
    image_url = Column(String(500), nullable=True)

    created_at = Column(TIMESTAMP(timezone=True), nullable=False, server_default=TS_DEFAULT)
    updated_at = Column(TIMESTAMP(timezone=True), nullable=False, server_default=TS_DEFAULT, onupdate=TS_ONUPDATE)

    creator_employee_id = Column(Integer, ForeignKey("users.id"), nullable=False)
    updated_employee_id = Column(Integer, ForeignKey("users.id"), nullable=False)

    creator = relationship("User", foreign_keys=[creator_employee_id], lazy="joined")
    updated_by = relationship("User", foreign_keys=[updated_employee_id], lazy="joined")

class BeaconEvents(Base):
    __tablename__ = "beacon_events"

    id = Column(Integer, primary_key=True, index=True)
    session_id =  Column(String(40), nullable=False) # Java UUID generation

    beacon_id = Column(Integer, ForeignKey("beacon.id", ondelete="SET NULL"), nullable=True, index=True)
    beacon_uuid = Column(String(100), nullable=False)
    beacon_major = Column(Integer, nullable=False)
    beacon_minor = Column(Integer, nullable=False)
    rssi = Column(Integer, nullable=False)
    tx_power = Column(Integer, nullable=False)

    recorded_at = Column(TIMESTAMP(timezone=True), nullable=False)
    received_at = Column(TIMESTAMP(timezone=True), nullable=False, server_default=TS_DEFAULT)

    __table_args__ = (
        Index("ix_beacon_events_beacon_recorded", "beacon_id", "recorded_at"),
        Index("ix_beacon_events_session_recorded", "session_id", "recorded_at"),
    )