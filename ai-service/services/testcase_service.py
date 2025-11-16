"""
Test Case Generation Service

Business logic for AI test case generation
"""
from typing import Dict, Any, List
import logging
from datetime import datetime
import uuid

from models.llm.rag_generator import RAGTestCaseGenerator
from data.mongodb_client import mongodb_client

logger = logging.getLogger(__name__)


class TestCaseGenerationService:
    """
    Service for AI test case generation

    Orchestrates generation pipeline and manages history
    """

    def __init__(self):
        self.generator = RAGTestCaseGenerator()

    def generate_testcases(self, request_data: Dict[str, Any]) -> Dict[str, Any]:
        """
        Generate test cases from requirement document

        Args:
            request_data: Request containing requirement and options

        Returns:
            Generation result with test cases
        """
        request_id = str(uuid.uuid4())
        requirement_text = request_data.get('requirement_text', '')
        module = request_data.get('module', 'unknown')
        num_cases = request_data.get('num_cases', 5)
        include_edge_cases = request_data.get('include_edge_cases', True)
        optimization_config = request_data.get('optimization', {})

        logger.info(
            f"Processing generation request {request_id} "
            f"for module: {module}, num_cases: {num_cases}"
        )

        # Validate input
        if not requirement_text:
            return {
                'success': False,
                'error': 'requirement_text is required',
                'request_id': request_id
            }

        # Generate test cases
        try:
            generation_result = self.generator.generate(
                requirement_text=requirement_text,
                module=module,
                num_cases=num_cases,
                include_edge_cases=include_edge_cases
            )

            # Apply optimization if requested
            if optimization_config:
                testcases = generation_result.get('testcases', [])
                optimized = self.generator.optimize_generated_cases(
                    testcases,
                    optimization_config
                )
                generation_result['testcases'] = optimized
                generation_result['optimized'] = True

            # Add request info
            generation_result['request_id'] = request_id
            generation_result['timestamp'] = datetime.utcnow().isoformat()

            # Save to history
            self._save_generation_history(
                request_id=request_id,
                request_data=request_data,
                result=generation_result
            )

            return generation_result

        except Exception as e:
            logger.error(f"Test case generation failed: {e}", exc_info=True)
            return {
                'success': False,
                'error': str(e),
                'request_id': request_id
            }

    def batch_generate(self, batch_request: Dict[str, Any]) -> Dict[str, Any]:
        """
        Batch generate test cases for multiple requirements

        Args:
            batch_request: Batch request data

        Returns:
            Batch results
        """
        requirements = batch_request.get('requirements', [])

        if not requirements:
            return {
                'success': False,
                'error': 'requirements list is empty'
            }

        logger.info(f"Processing batch generation for {len(requirements)} requirements")

        results = []
        for req in requirements:
            result = self.generate_testcases(req)
            results.append(result)

        return {
            'success': True,
            'total_requests': len(requirements),
            'successful': sum(1 for r in results if r.get('success')),
            'failed': sum(1 for r in results if not r.get('success')),
            'results': results
        }

    def update_user_feedback(
        self,
        request_id: str,
        feedback: Dict[str, Any]
    ) -> Dict[str, Any]:
        """
        Update user feedback for generated test cases

        Args:
            request_id: Generation request ID
            feedback: User feedback data

        Returns:
            Update result
        """
        logger.info(f"Updating feedback for request: {request_id}")

        try:
            success = mongodb_client.update_user_feedback(request_id, feedback)

            if success:
                return {
                    'success': True,
                    'request_id': request_id,
                    'message': 'Feedback updated successfully'
                }
            else:
                return {
                    'success': False,
                    'request_id': request_id,
                    'error': 'Failed to update feedback'
                }

        except Exception as e:
            logger.error(f"Failed to update feedback: {e}")
            return {
                'success': False,
                'request_id': request_id,
                'error': str(e)
            }

    def _save_generation_history(
        self,
        request_id: str,
        request_data: Dict[str, Any],
        result: Dict[str, Any]
    ):
        """Save generation history to MongoDB"""
        try:
            history_data = {
                'requestId': request_id,
                'module': request_data.get('module', 'unknown'),
                'num_cases_requested': request_data.get('num_cases', 5),
                'num_cases_generated': result.get('total_unique', 0),
                'include_edge_cases': request_data.get('include_edge_cases', True),
                'optimization_config': request_data.get('optimization', {}),
                'success': result.get('success', False),
                'metadata': result.get('metadata', {}),
                'duplicate_count': result.get('total_duplicates', 0)
            }

            mongodb_client.save_testcase_generation_history(history_data)
            logger.info(f"Saved generation history for request: {request_id}")

        except Exception as e:
            logger.error(f"Failed to save generation history: {e}")


class TestCaseOptimizationService:
    """
    Service for test case optimization operations

    Provides deduplication, prioritization, and quality improvement
    """

    def __init__(self):
        self.generator = RAGTestCaseGenerator()

    def deduplicate(
        self,
        testcases: List[Dict[str, Any]],
        threshold: float = 0.85
    ) -> Dict[str, Any]:
        """
        Deduplicate test cases

        Args:
            testcases: Test cases to deduplicate
            threshold: Similarity threshold

        Returns:
            Deduplication result
        """
        logger.info(f"Deduplicating {len(testcases)} test cases")

        from models.optimization.deduplicator import SemanticDeduplicator

        deduplicator = SemanticDeduplicator(similarity_threshold=threshold)
        unique_cases, duplicate_groups = deduplicator.deduplicate(testcases)

        return {
            'success': True,
            'original_count': len(testcases),
            'unique_count': len(unique_cases),
            'duplicates_removed': len(testcases) - len(unique_cases),
            'unique_testcases': unique_cases,
            'duplicate_groups': duplicate_groups
        }

    def prioritize(
        self,
        testcases: List[Dict[str, Any]],
        custom_weights: Dict[str, float] = None
    ) -> Dict[str, Any]:
        """
        Prioritize test cases

        Args:
            testcases: Test cases to prioritize
            custom_weights: Optional custom weights

        Returns:
            Prioritization result
        """
        logger.info(f"Prioritizing {len(testcases)} test cases")

        from models.optimization.prioritizer import TestCasePrioritizer

        prioritizer = TestCasePrioritizer()

        if custom_weights:
            prioritizer.adjust_weights(custom_weights)

        prioritized = prioritizer.prioritize(testcases)

        return {
            'success': True,
            'total_cases': len(testcases),
            'prioritized_testcases': prioritized,
            'weights_used': prioritizer.weights
        }

    def analyze_quality(
        self,
        testcases: List[Dict[str, Any]]
    ) -> Dict[str, Any]:
        """
        Analyze test case quality metrics

        Args:
            testcases: Test cases to analyze

        Returns:
            Quality analysis result
        """
        logger.info(f"Analyzing quality of {len(testcases)} test cases")

        # Calculate metrics
        total = len(testcases)
        priority_dist = self._calculate_priority_distribution(testcases)
        type_dist = self._calculate_type_distribution(testcases)
        avg_steps = sum(len(tc.get('steps', [])) for tc in testcases) / max(total, 1)
        completeness = self._calculate_completeness(testcases)

        return {
            'success': True,
            'total_cases': total,
            'priority_distribution': priority_dist,
            'type_distribution': type_dist,
            'average_steps_per_case': round(avg_steps, 2),
            'completeness_score': round(completeness, 2),
            'quality_grade': self._calculate_quality_grade(completeness)
        }

    def _calculate_priority_distribution(
        self,
        testcases: List[Dict[str, Any]]
    ) -> Dict[str, int]:
        """Calculate priority distribution"""
        dist = {'P0': 0, 'P1': 0, 'P2': 0, 'P3': 0}
        for tc in testcases:
            priority = tc.get('priority', 'P3')
            if priority in dist:
                dist[priority] += 1
        return dist

    def _calculate_type_distribution(
        self,
        testcases: List[Dict[str, Any]]
    ) -> Dict[str, int]:
        """Calculate test type distribution"""
        dist = {}
        for tc in testcases:
            test_type = tc.get('type', '功能测试')
            dist[test_type] = dist.get(test_type, 0) + 1
        return dist

    def _calculate_completeness(self, testcases: List[Dict[str, Any]]) -> float:
        """Calculate completeness score"""
        if not testcases:
            return 0.0

        scores = []
        for tc in testcases:
            score = 0.0
            # Has name
            if tc.get('name'):
                score += 0.2
            # Has steps
            if tc.get('steps'):
                score += 0.3
            # Has expected results in steps
            if any(s.get('expected') for s in tc.get('steps', [])):
                score += 0.2
            # Has preconditions
            if tc.get('preconditions'):
                score += 0.15
            # Has tags
            if tc.get('tags'):
                score += 0.15

            scores.append(score)

        return sum(scores) / len(scores)

    def _calculate_quality_grade(self, completeness: float) -> str:
        """Calculate quality grade from completeness score"""
        if completeness >= 0.9:
            return 'A'
        elif completeness >= 0.75:
            return 'B'
        elif completeness >= 0.6:
            return 'C'
        else:
            return 'D'
