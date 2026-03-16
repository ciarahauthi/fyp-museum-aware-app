from fastapi import APIRouter, UploadFile, File, HTTPException, Depends
from pathlib import Path
import os
import re

from api.core.auth import get_current_user

router = APIRouter()

STATIC_DIR = Path("api/static")
ALLOWED_FOLDERS = {"exhibits", "routes", "home"}
IMAGE_EXTENSIONS = {".jpg", ".jpeg", ".png"}


def sanitize_filename(filename: str) -> str:
    name, ext = os.path.splitext(filename)
    # make name lowercase, replace spaces with underscores
    name = name.lower()
    name = re.sub(r"[^\w\-]", "_", name)
    name = re.sub(r"_+", "_", name).strip("_")
    return f"{name}{ext.lower()}"


@router.get("/{folder}")
def list_images(folder: str):
    if folder not in ALLOWED_FOLDERS:
        raise HTTPException(status_code=400, detail="Invalid folder")
    folder_path = STATIC_DIR / folder
    if not folder_path.exists():
        return []
    files = [
        {"filename": f, "url": f"/api/static/{folder}/{f}"}
        for f in sorted(os.listdir(folder_path))
        if Path(f).suffix.lower() in IMAGE_EXTENSIONS
    ]
    return files


@router.post("/{folder}")
async def upload_image(folder: str, file: UploadFile = File(...), current_user=Depends(get_current_user)):
    if folder not in ALLOWED_FOLDERS:
        raise HTTPException(status_code=400, detail="Invalid folder")
    
    safe_name = sanitize_filename(file.filename)

    if Path(safe_name).suffix.lower() not in IMAGE_EXTENSIONS:
        raise HTTPException(status_code=400, detail="Invalid file type. Allowed: jpg, jpeg, png")
    
    folder_path = STATIC_DIR / folder
    folder_path.mkdir(parents=True, exist_ok=True)
    file_path = folder_path / safe_name
    content = await file.read()

    with open(file_path, "wb") as f:
        f.write(content)
        
    return {"filename": safe_name, "url": f"/api/static/{folder}/{safe_name}"}
