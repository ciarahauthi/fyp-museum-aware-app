from pydantic import BaseModel, ConfigDict
from datetime import datetime

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
    creator_employee_id: int
    updated_employee_id: int
    created_at: datetime
    updated_at: datetime
    model_config = ConfigDict(from_attributes=True)