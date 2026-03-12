from pydantic import BaseModel, ConfigDict
from datetime import datetime
from typing import Optional

'''
Contains all the beacon schemas
'''

# Beacons
class BeaconCreate(BaseModel):
    name: str
    uuid: str
    major: int
    minor: int
    description: str

class BeaconRead(BeaconCreate):
    id: int
    location_id: int
    creator_employee_id: int
    updated_employee_id: int
    created_at: datetime
    updated_at: datetime
    model_config = ConfigDict(from_attributes=True)

class BeaconUpdate(BaseModel):
    name: Optional[str] = None
    uuid: Optional[str] = None
    major: Optional[int] = None
    minor: Optional[int] = None
    description: Optional[str] = None
    location_id: Optional[int] = None