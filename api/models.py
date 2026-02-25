from database import Base
from sqlalchemy import Column, Integer, String, Boolean, TIMESTAMP, text, ForeignKey, Numeric, JSON
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
    location_id = Column(Integer, ForeignKey("node.id"), nullable=False)

    employee_id = Column(Integer, ForeignKey("users.id"), nullable=False)

''' 
Nodes are locations.
Location should be treated as a room. Each "Node" is a representation of the room in the graph.

X = Coord x on the map on the phone.
Y = Coord y on the map on the phone.
'''
class Node(Base):
    __tablename__ = "node"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String(100), unique=True, nullable=False)
    x = Column(Numeric(3, 2), nullable=False)
    y = Column(Numeric(3, 2), nullable=False)

    # weight = Column(Integer, nullable=False, server_default="0")

class Edge(Base):
    __tablename__ = "edges"

    id = Column(Integer, primary_key=True, index=True)
    node_id = Column(Integer, ForeignKey("node.id", ondelete="CASCADE"), nullable=False)
    connected_node_id = Column(Integer, ForeignKey("node.id", ondelete="CASCADE"), nullable=False)
    weight = Column(Integer, nullable=False, server_default="0")

    node_obj = relationship("Node", foreign_keys=[node_id], lazy="joined")
    connected_node_obj = relationship("Node", foreign_keys=[connected_node_id],lazy="joined")

class Route(Base):
    __tablename__ = "route"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String(100), unique=True, nullable=False)
    description = Column(String(2000), nullable=False)

    node_ids = Column(JSON, nullable=False)  # Stored as a list of node ids [7, 3, 1, 4, 2]
    stops = Column(JSON, nullable=False)  # Stored as a list of exhibit ids [7, 3, 1, 4, 2]
