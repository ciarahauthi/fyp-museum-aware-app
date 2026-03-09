from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy import tuple_
from sqlalchemy.orm import Session
from datetime import timezone, datetime

from api.schemas.beacon_events import BeaconEventsRequest
from api.db.database import get_db
from api.db.models import BeaconEvents, Beacon

router = APIRouter()

@router.post("", status_code=status.HTTP_201_CREATED)
def upload_beacon_events(payload: BeaconEventsRequest, db: Session = Depends(get_db)):
    if not payload.events:
        return {"inserted": 0}

    if len(payload.events) > 5000:
        raise HTTPException(status_code=413, detail="Too many events in one request")

    keys = {(e.beacon_uuid, e.beacon_major, e.beacon_minor) for e in payload.events}

    beacon_rows = (
        db.query(Beacon.id, Beacon.uuid, Beacon.major, Beacon.minor)
        .filter(tuple_(Beacon.uuid, Beacon.major, Beacon.minor).in_(list(keys)))
        .all()
    )
    beacon_map = {(b.uuid, b.major, b.minor): b.id for b in beacon_rows}

    rows = []
    for e in payload.events:
        recorded_dt = datetime.fromtimestamp(e.recorded_at / 1000.0, tz=timezone.utc)

        rows.append(
            BeaconEvents(
                session_id=payload.session_id,
                beacon_id=beacon_map.get((e.beacon_uuid, e.beacon_major, e.beacon_minor)),
                beacon_uuid=e.beacon_uuid,
                beacon_major=e.beacon_major,
                beacon_minor=e.beacon_minor,
                rssi=e.rssi,
                tx_power=e.tx_power,
                recorded_at=recorded_dt,
                # received_at handled by server_default
            )
        )

    db.add_all(rows)
    db.commit()

    return {"inserted": len(rows)}