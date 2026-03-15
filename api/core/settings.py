from pydantic_settings import BaseSettings, SettingsConfigDict
from functools import lru_cache

class Settings(BaseSettings):
    dbUrl: str 
    secretKey: str
    frontendUrl: str
    
    model_config = SettingsConfigDict(env_file="api/.env")

@lru_cache
def get_settings():
    return Settings()