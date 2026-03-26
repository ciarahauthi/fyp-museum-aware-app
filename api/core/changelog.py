from sqlalchemy.orm import Session
from api.db.models import Changes, ChangeType


def log_change(
    db: Session,
    change: ChangeType,
    table_name: str,
    details: dict,
    employee_id: int
):
    entry = Changes(
        change=change,
        table_name=table_name,
        details=details,
        employee_id=employee_id,
    )
    db.add(entry)
    db.commit()
