from pydantic import BaseModel, ConfigDict
from datetime import datetime
from typing import Any
from api.db.models import ChangeType

class ChangeRead(BaseModel):
    id: int
    change: ChangeType
    table_name: str
    details: dict[str, Any]
    timestamp: datetime
    employee_id: int | None = None
    model_config = ConfigDict(from_attributes=True)

class ChangeCreate(BaseModel):
    change: ChangeType
    table_name: str
    details: dict[str, Any]
    employee_id: int
