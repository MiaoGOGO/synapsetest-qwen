"""
AI驱动测试任务管理系统 - AI服务
FastAPI主应用
"""
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
import uvicorn
import logging
import sys

from config import settings, ai_config
from api import recommendation, testcase
from data.mongodb_client import mongodb_client
from data.redis_client import redis_client

# 配置日志
logging.basicConfig(
    level=getattr(logging, settings.log_level),
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.StreamHandler(sys.stdout)
    ]
)

logger = logging.getLogger(__name__)

# 创建FastAPI应用
app = FastAPI(
    title=settings.app_name,
    description="AI服务用于测试用例生成、智能调度推荐和风险评估",
    version=settings.version,
    debug=settings.debug
)

# CORS配置
app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.cors_origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# 启动事件
@app.on_event("startup")
async def startup_event():
    """应用启动时执行"""
    logger.info(f"Starting {settings.app_name} v{settings.version}")
    logger.info(f"LLM Provider: {ai_config.LLM_PROVIDER}")
    logger.info(f"Debug Mode: {settings.debug}")

    # 测试数据库连接
    try:
        if mongodb_client.db is not None:
            logger.info("MongoDB connection established")
        else:
            logger.warning("MongoDB not available, some features will be limited")
    except Exception as e:
        logger.warning(f"MongoDB connection check failed: {e}")

    try:
        if redis_client._client is not None:
            logger.info("Redis connection established")
        else:
            logger.warning("Redis not available, using in-memory cache")
    except Exception as e:
        logger.warning(f"Redis connection check failed: {e}")


# 关闭事件
@app.on_event("shutdown")
async def shutdown_event():
    """应用关闭时执行"""
    logger.info("Shutting down AI Service")

    # 关闭数据库连接
    try:
        mongodb_client.close()
        logger.info("MongoDB connection closed")
    except Exception as e:
        logger.error(f"Error closing MongoDB: {e}")

    try:
        redis_client.close()
        logger.info("Redis connection closed")
    except Exception as e:
        logger.error(f"Error closing Redis: {e}")


# 注册路由
app.include_router(
    recommendation.router,
    prefix=ai_config.API_PREFIX
)

app.include_router(
    testcase.router,
    prefix=ai_config.API_PREFIX
)


@app.get("/health")
async def health_check():
    """健康检查端点"""
    return {
        "status": "UP",
        "service": "ai-service",
        "version": settings.version,
        "llm_provider": ai_config.LLM_PROVIDER
    }


@app.get("/")
async def root():
    """根路径"""
    return {
        "message": settings.app_name,
        "version": settings.version,
        "docs": "/docs",
        "health": "/health",
        "api_prefix": ai_config.API_PREFIX,
        "endpoints": {
            "recommendation": f"{ai_config.API_PREFIX}/recommendation",
            "testcase": f"{ai_config.API_PREFIX}/testcase"
        }
    }

if __name__ == "__main__":
    uvicorn.run(
        "main:app",
        host="0.0.0.0",
        port=8000,
        reload=True
    )
