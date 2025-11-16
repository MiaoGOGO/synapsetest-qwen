"""
AI Service Configuration Management
"""
import os
from typing import Optional

class AIConfig:
    """AI Service Configuration"""

    # Model paths
    QWEN_MODEL_PATH: str = os.getenv('QWEN_MODEL_PATH', '/models/Qwen-7B-Chat')
    SENTENCE_BERT_MODEL: str = 'paraphrase-multilingual-mpnet-base-v2'
    XGBOOST_MODEL_PATH: str = os.getenv('XGBOOST_MODEL_PATH', '/models/xgboost_models/')

    # Model runtime configuration
    USE_GPU: bool = os.getenv('USE_GPU', 'false').lower() == 'true'
    MAX_GPU_MEMORY: str = os.getenv('MAX_GPU_MEMORY', '16GB')
    DEVICE_MAP: str = 'auto'

    # Inference configuration
    MAX_TOKENS: int = int(os.getenv('MAX_TOKENS', '2048'))
    TEMPERATURE: float = float(os.getenv('TEMPERATURE', '0.7'))
    TOP_P: float = float(os.getenv('TOP_P', '0.9'))

    # Vector database configuration (Milvus)
    MILVUS_HOST: str = os.getenv('MILVUS_HOST', 'localhost')
    MILVUS_PORT: int = int(os.getenv('MILVUS_PORT', '19530'))

    # MongoDB configuration
    MONGODB_URI: str = os.getenv('MONGODB_URI', 'mongodb://localhost:27017')
    MONGODB_DB: str = os.getenv('MONGODB_DB', 'synapsetest_ai')

    # Redis configuration
    REDIS_HOST: str = os.getenv('REDIS_HOST', 'localhost')
    REDIS_PORT: int = int(os.getenv('REDIS_PORT', '6379'))
    REDIS_PASSWORD: Optional[str] = os.getenv('REDIS_PASSWORD', None)
    REDIS_DB: int = int(os.getenv('REDIS_DB', '0'))

    # API configuration
    API_PREFIX: str = '/api/v1/ai'

    # Semantic deduplication
    SIMILARITY_THRESHOLD: float = float(os.getenv('SIMILARITY_THRESHOLD', '0.85'))

    # Cache configuration
    CACHE_TTL: int = int(os.getenv('CACHE_TTL', '300'))  # 5 minutes
    CACHE_MAX_SIZE: int = int(os.getenv('CACHE_MAX_SIZE', '1000'))

    # LLM Provider (local or api)
    LLM_PROVIDER: str = os.getenv('LLM_PROVIDER', 'mock')  # mock, local, api
    LLM_API_KEY: Optional[str] = os.getenv('LLM_API_KEY', None)
    LLM_API_BASE: Optional[str] = os.getenv('LLM_API_BASE', None)

    @classmethod
    def get_mongodb_settings(cls) -> dict:
        """Get MongoDB connection settings"""
        return {
            'uri': cls.MONGODB_URI,
            'database': cls.MONGODB_DB
        }

    @classmethod
    def get_redis_settings(cls) -> dict:
        """Get Redis connection settings"""
        return {
            'host': cls.REDIS_HOST,
            'port': cls.REDIS_PORT,
            'password': cls.REDIS_PASSWORD,
            'db': cls.REDIS_DB
        }

    @classmethod
    def get_milvus_settings(cls) -> dict:
        """Get Milvus connection settings"""
        return {
            'host': cls.MILVUS_HOST,
            'port': cls.MILVUS_PORT
        }


class Settings:
    """Application settings"""

    app_name: str = "SynapseTest AI Service"
    version: str = "1.0.0"
    debug: bool = os.getenv('DEBUG', 'false').lower() == 'true'

    # CORS settings
    cors_origins: list = os.getenv('CORS_ORIGINS', '*').split(',')

    # Logging
    log_level: str = os.getenv('LOG_LEVEL', 'INFO')


# Singleton instances
ai_config = AIConfig()
settings = Settings()
