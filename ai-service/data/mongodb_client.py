"""
MongoDB Client for AI Service
"""
from typing import Optional, List, Dict, Any
from datetime import datetime
import logging

try:
    from pymongo import MongoClient
    from pymongo.database import Database
    from pymongo.collection import Collection
    PYMONGO_AVAILABLE = True
except ImportError:
    PYMONGO_AVAILABLE = False

from config import ai_config

logger = logging.getLogger(__name__)


class MongoDBClient:
    """MongoDB client for AI training data and history storage"""

    _instance: Optional['MongoDBClient'] = None
    _client: Optional[Any] = None
    _db: Optional[Any] = None

    def __new__(cls):
        if cls._instance is None:
            cls._instance = super().__new__(cls)
        return cls._instance

    def __init__(self):
        if not PYMONGO_AVAILABLE:
            logger.warning("pymongo not available, using mock mode")
            return

        if self._client is None:
            try:
                self._client = MongoClient(ai_config.MONGODB_URI)
                self._db = self._client[ai_config.MONGODB_DB]
                logger.info(f"Connected to MongoDB: {ai_config.MONGODB_DB}")
            except Exception as e:
                logger.error(f"Failed to connect to MongoDB: {e}")
                self._client = None
                self._db = None

    @property
    def db(self) -> Optional[Any]:
        return self._db

    def get_collection(self, name: str) -> Optional[Any]:
        """Get a collection by name"""
        if self._db is None:
            return None
        return self._db[name]

    def save_recommendation_history(self, data: Dict[str, Any]) -> Optional[str]:
        """Save recommendation history"""
        if self._db is None:
            logger.warning("MongoDB not available, skipping save")
            return None

        collection = self._db['recommendation_history']
        data['timestamp'] = datetime.utcnow()
        result = collection.insert_one(data)
        return str(result.inserted_id)

    def save_testcase_generation_history(self, data: Dict[str, Any]) -> Optional[str]:
        """Save test case generation history"""
        if self._db is None:
            logger.warning("MongoDB not available, skipping save")
            return None

        collection = self._db['testcase_generation_history']
        data['timestamp'] = datetime.utcnow()
        result = collection.insert_one(data)
        return str(result.inserted_id)

    def get_similar_testcases(self, module: str, limit: int = 5) -> List[Dict[str, Any]]:
        """Get similar historical test cases for a module"""
        if self._db is None:
            return []

        collection = self._db['historical_testcases']
        cases = collection.find(
            {'module': module},
            limit=limit,
            sort=[('created_at', -1)]
        )
        return list(cases)

    def get_company_standards(self) -> Dict[str, Any]:
        """Get company testing standards"""
        if self._db is None:
            return self._get_default_standards()

        collection = self._db['company_standards']
        standards = collection.find_one({'active': True})
        if standards:
            return standards
        return self._get_default_standards()

    def _get_default_standards(self) -> Dict[str, Any]:
        """Return default testing standards"""
        return {
            'naming_convention': 'descriptive_action_expected',
            'priority_levels': ['P0', 'P1', 'P2', 'P3'],
            'test_types': ['功能测试', '性能测试', '安全测试', '兼容性测试'],
            'required_fields': ['name', 'steps', 'expected_result', 'priority']
        }

    def update_user_feedback(
        self,
        request_id: str,
        feedback: Dict[str, Any]
    ) -> bool:
        """Update user feedback for a generation request"""
        if self._db is None:
            return False

        collection = self._db['testcase_generation_history']
        result = collection.update_one(
            {'requestId': request_id},
            {'$set': {'userFeedback': feedback}}
        )
        return result.modified_count > 0

    def close(self):
        """Close MongoDB connection"""
        if self._client:
            self._client.close()
            self._client = None
            self._db = None


# Singleton instance
mongodb_client = MongoDBClient()
