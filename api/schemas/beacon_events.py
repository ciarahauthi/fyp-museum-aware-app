from pydantic import BaseModel, Field
from typing import List

'''
Contains all the beacon event schemas
'''

class BeaconEventIn(BaseModel):
    beacon_uuid: str
    beacon_major: int
    beacon_minor: int
    rssi: int
    tx_power: int
    recorded_at: int = Field(..., description="Epoch milliseconds from device")

class BeaconEventsRequest(BaseModel):
    session_id: str
    events: List[BeaconEventIn]