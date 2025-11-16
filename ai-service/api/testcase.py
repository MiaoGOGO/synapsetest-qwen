"""
Test Case Generation API Routes
"""
from fastapi import APIRouter, HTTPException
from pydantic import BaseModel, Field
from typing import Dict, Any, List, Optional
import logging

from services.testcase_service import (
    TestCaseGenerationService,
    TestCaseOptimizationService
)

logger = logging.getLogger(__name__)

router = APIRouter(prefix="/testcase", tags=["testcase"])

# Initialize services
generation_service = TestCaseGenerationService()
optimization_service = TestCaseOptimizationService()


# Request/Response Models
class OptimizationConfig(BaseModel):
    deduplicate: bool = Field(default=True)
    prioritize: bool = Field(default=True)
    min_priority: Optional[str] = Field(default=None, pattern="^P[0-3]$")
    max_cases: Optional[int] = Field(default=None, ge=1)


class GenerationRequest(BaseModel):
    requirement_text: str = Field(..., min_length=10, description="Requirement document")
    module: str = Field(default="unknown", description="Module name")
    num_cases: int = Field(default=5, ge=1, le=50, description="Number of cases to generate")
    include_edge_cases: bool = Field(default=True)
    optimization: Optional[OptimizationConfig] = Field(default=None)


class BatchGenerationRequest(BaseModel):
    requirements: List[GenerationRequest] = Field(..., min_items=1, max_items=20)


class FeedbackRequest(BaseModel):
    request_id: str = Field(..., description="Generation request ID")
    rating: int = Field(..., ge=1, le=5, description="User rating (1-5)")
    comments: Optional[str] = Field(default=None)
    accepted_cases: List[str] = Field(default=[], description="List of accepted case names")
    rejected_cases: List[str] = Field(default=[], description="List of rejected case names")


class DeduplicationRequest(BaseModel):
    testcases: List[Dict[str, Any]] = Field(..., min_items=1)
    threshold: float = Field(default=0.85, ge=0.0, le=1.0)


class PrioritizationRequest(BaseModel):
    testcases: List[Dict[str, Any]] = Field(..., min_items=1)
    custom_weights: Optional[Dict[str, float]] = Field(default=None)


@router.post("/generate")
async def generate_testcases(request: GenerationRequest):
    """
    Generate test cases from requirement document

    Uses RAG approach with LLM + historical cases + company standards
    """
    try:
        logger.info(f"Received generation request for module: {request.module}")

        result = generation_service.generate_testcases(request.dict())

        return result

    except Exception as e:
        logger.error(f"Test case generation failed: {e}", exc_info=True)
        raise HTTPException(status_code=500, detail=str(e))


@router.post("/generate/batch")
async def batch_generate(request: BatchGenerationRequest):
    """
    Batch generate test cases for multiple requirements
    """
    try:
        logger.info(f"Received batch generation request for {len(request.requirements)} requirements")

        result = generation_service.batch_generate(request.dict())

        return result

    except Exception as e:
        logger.error(f"Batch generation failed: {e}", exc_info=True)
        raise HTTPException(status_code=500, detail=str(e))


@router.post("/feedback")
async def submit_feedback(request: FeedbackRequest):
    """
    Submit user feedback for generated test cases

    Helps improve model through reinforcement learning
    """
    try:
        result = generation_service.update_user_feedback(
            request_id=request.request_id,
            feedback=request.dict()
        )

        return result

    except Exception as e:
        logger.error(f"Feedback submission failed: {e}", exc_info=True)
        raise HTTPException(status_code=500, detail=str(e))


@router.post("/optimize/deduplicate")
async def deduplicate_testcases(request: DeduplicationRequest):
    """
    Deduplicate test cases using semantic similarity
    """
    try:
        result = optimization_service.deduplicate(
            testcases=request.testcases,
            threshold=request.threshold
        )

        return result

    except Exception as e:
        logger.error(f"Deduplication failed: {e}", exc_info=True)
        raise HTTPException(status_code=500, detail=str(e))


@router.post("/optimize/prioritize")
async def prioritize_testcases(request: PrioritizationRequest):
    """
    Prioritize test cases using multi-factor scoring
    """
    try:
        result = optimization_service.prioritize(
            testcases=request.testcases,
            custom_weights=request.custom_weights
        )

        return result

    except Exception as e:
        logger.error(f"Prioritization failed: {e}", exc_info=True)
        raise HTTPException(status_code=500, detail=str(e))


@router.post("/analyze/quality")
async def analyze_quality(testcases: List[Dict[str, Any]]):
    """
    Analyze quality metrics of test cases
    """
    try:
        result = optimization_service.analyze_quality(testcases)

        return result

    except Exception as e:
        logger.error(f"Quality analysis failed: {e}", exc_info=True)
        raise HTTPException(status_code=500, detail=str(e))


@router.get("/health")
async def health_check():
    """Health check endpoint"""
    return {
        'status': 'UP',
        'service': 'testcase-generation',
        'llm_available': True
    }
