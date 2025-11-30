"""
Vector Database Client

Milvus client for vector similarity search
"""
from typing import Optional
import logging

from data.milvus_client import milvus_client

logger = logging.getLogger(__name__)


# Milvus client instance
vector_db_client = milvus_client

logger.info("Using Milvus as vector database (enterprise-grade)")

