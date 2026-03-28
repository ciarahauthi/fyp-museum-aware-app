from fastapi import APIRouter, Depends, Query
from sqlalchemy.orm import Session
from sqlalchemy import text
from datetime import date

from api.db.database import get_db
from api.core.auth import get_current_user

router = APIRouter()

HOUR_LABELS = [f"{h:02d}:00" for h in range(24)]
DOW_ORDER = [2, 3, 4, 5, 6, 7, 1]  # Mon first (1=Sun, 2=Mon ... 7=Sat)
DOW_LABELS = {1: "Sun", 2: "Mon", 3: "Tue", 4: "Wed", 5: "Thu", 6: "Fri", 7: "Sat"}


@router.get("/busy-times/hourly")
def busy_times_hourly(start: date, end: date, db: Session = Depends(get_db), _=Depends(get_current_user)):
    sql = text("""
        SELECT hour, ROUND(AVG(cnt), 2) AS value
        FROM (
            SELECT DATE(recorded_at) AS day, HOUR(recorded_at) AS hour,
                   COUNT(DISTINCT session_id) AS cnt
            FROM beacon_events
            WHERE DATE(recorded_at) BETWEEN :start AND :end
            GROUP BY day, hour
        ) sub
        GROUP BY hour
        ORDER BY hour
    """)
    rows = db.execute(sql, {"start": start, "end": end}).fetchall()
    result = {row.hour: float(row.value) for row in rows}
    return [{"bucket": HOUR_LABELS[h], "value": result.get(h, 0)} for h in range(24)]


@router.get("/busy-times/weekday")
def busy_times_weekday(start: date, end: date, db: Session = Depends(get_db), _=Depends(get_current_user)):
    sql = text("""
        SELECT dow, ROUND(AVG(cnt), 2) AS value
        FROM (
            SELECT DATE(recorded_at) AS day, DAYOFWEEK(recorded_at) AS dow,
                   COUNT(DISTINCT session_id) AS cnt
            FROM beacon_events
            WHERE DATE(recorded_at) BETWEEN :start AND :end
            GROUP BY day, dow
        ) sub
        GROUP BY dow
        ORDER BY dow
    """)
    rows = db.execute(sql, {"start": start, "end": end}).fetchall()
    result = {row.dow: float(row.value) for row in rows}
    return [{"bucket": DOW_LABELS[d], "value": result.get(d, 0)} for d in DOW_ORDER]


@router.get("/busy-times/daily")
def busy_times_daily(start: date, end: date, db: Session = Depends(get_db), _=Depends(get_current_user)):
    sql = text("""
        SELECT DATE(recorded_at) AS bucket, COUNT(DISTINCT session_id) AS value
        FROM beacon_events
        WHERE DATE(recorded_at) BETWEEN :start AND :end
        GROUP BY DATE(recorded_at)
        ORDER BY bucket
    """)
    rows = db.execute(sql, {"start": start, "end": end}).fetchall()
    return [{"bucket": str(row.bucket), "value": int(row.value)} for row in rows]


@router.get("/busy-times/monthly")
def busy_times_monthly(start: date, end: date, db: Session = Depends(get_db), _=Depends(get_current_user)):
    sql = text("""
        SELECT DATE_FORMAT(recorded_at, '%Y-%m') AS bucket,
               COUNT(DISTINCT session_id) AS value
        FROM beacon_events
        WHERE DATE(recorded_at) BETWEEN :start AND :end
        GROUP BY DATE_FORMAT(recorded_at, '%Y-%m')
        ORDER BY bucket
    """)
    rows = db.execute(sql, {"start": start, "end": end}).fetchall()
    return [{"bucket": row.bucket, "value": int(row.value)} for row in rows]


@router.get("/popular-exhibits")
def popular_exhibits(
    start: date,
    end: date,
    limit: int = Query(default=10, ge=1, le=50),
    db: Session = Depends(get_db),
    _=Depends(get_current_user),
):
    sql = text("""
        SELECT e.id, e.title, e.beacon_id,
               COUNT(DISTINCT be.session_id) AS unique_visitors
        FROM exhibit e
        LEFT JOIN beacon_events be
               ON be.beacon_id = e.beacon_id
              AND DATE(be.recorded_at) BETWEEN :start AND :end
        WHERE e.active = 1
        GROUP BY e.id, e.title, e.beacon_id
        ORDER BY unique_visitors DESC, e.title ASC
        LIMIT :lim
    """)
    rows = db.execute(sql, {"start": start, "end": end, "lim": limit}).fetchall()
    return [
        {
            "exhibit_id": row.id,
            "title": row.title,
            "unique_visitors": None if row.beacon_id is None else int(row.unique_visitors),
        }
        for row in rows
    ]


@router.get("/popular-rooms")
def popular_rooms(start: date, end: date, db: Session = Depends(get_db), _=Depends(get_current_user)):
    sql = text("""
        SELECT n.id, n.name, COUNT(DISTINCT be.session_id) AS unique_visitors
        FROM node n
        LEFT JOIN beacon b ON b.location_id = n.id
        LEFT JOIN beacon_events be
               ON be.beacon_id = b.id
              AND DATE(be.recorded_at) BETWEEN :start AND :end
        GROUP BY n.id, n.name
        ORDER BY unique_visitors DESC
    """)
    rows = db.execute(sql, {"start": start, "end": end}).fetchall()
    return [
        {"room_id": row.id, "name": row.name, "unique_visitors": int(row.unique_visitors)}
        for row in rows
    ]
