"""
Risk Prediction Model

Predicts risk level (LOW, MEDIUM, HIGH, CRITICAL) for code changes.
"""
from typing import Dict, Any, Optional, List
import logging

from utils.feature_extractor import RiskFeatureExtractor

logger = logging.getLogger(__name__)


class RiskPredictionModel:
    """
    Predicts risk level for code changes using XGBoost classifier

    Risk Levels:
    - LOW: No defects or minor issues (P3)
    - MEDIUM: General defects (P2)
    - HIGH: Severe defects (P0/P1)
    - CRITICAL: Production incidents
    """

    def __init__(self):
        self.feature_extractor = RiskFeatureExtractor()
        self.model = self._load_model()
        self.risk_levels = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL']

    def _load_model(self) -> Optional[Any]:
        """Load risk prediction model"""
        try:
            import xgboost as xgb
            from config import ai_config
            import os

            model_path = os.path.join(
                ai_config.XGBOOST_MODEL_PATH,
                'risk_predictor.json'
            )

            if os.path.exists(model_path):
                model = xgb.XGBClassifier()
                model.load_model(model_path)
                logger.info(f"Loaded risk model from {model_path}")
                return model
            else:
                logger.warning("Risk model not found, using heuristic mode")
                return None
        except Exception as e:
            logger.error(f"Failed to load risk model: {e}")
            return None

    def predict_risk(self, code_change: Dict[str, Any]) -> Dict[str, Any]:
        """
        Predict risk level for code change

        Args:
            code_change: Code change information

        Returns:
            Risk assessment with level, confidence, and factors
        """
        features = self.feature_extractor.extract_risk_features(code_change)

        if self.model is not None:
            risk_result = self._ml_predict(features)
        else:
            risk_result = self._heuristic_predict(features, code_change)

        # Add risk factors
        risk_result['risk_factors'] = self._analyze_risk_factors(code_change)
        risk_result['mitigation_suggestions'] = self._suggest_mitigations(
            risk_result['risk_level']
        )

        return risk_result

    def _ml_predict(self, features: List[float]) -> Dict[str, Any]:
        """Use ML model for risk prediction"""
        import numpy as np

        X = np.array([features])
        probabilities = self.model.predict_proba(X)[0]
        predicted_class = int(np.argmax(probabilities))

        return {
            'risk_level': self.risk_levels[predicted_class],
            'confidence': float(max(probabilities)),
            'probabilities': {
                level: float(prob)
                for level, prob in zip(self.risk_levels, probabilities)
            }
        }

    def _heuristic_predict(
        self,
        features: List[float],
        code_change: Dict[str, Any]
    ) -> Dict[str, Any]:
        """Heuristic risk prediction when model unavailable"""
        # Extract key indicators
        changed_files = features[0]
        changed_lines = features[1]
        complexity_delta = features[2]
        module_risk = features[6]
        change_type_risk = features[7]

        # Calculate risk score
        risk_score = (
            0.2 * min(1.0, changed_files / 20) +
            0.2 * min(1.0, changed_lines / 500) +
            0.2 * min(1.0, complexity_delta / 10) +
            0.25 * module_risk +
            0.15 * change_type_risk
        )

        # Map score to risk level
        if risk_score > 0.75:
            risk_level = 'CRITICAL'
        elif risk_score > 0.55:
            risk_level = 'HIGH'
        elif risk_score > 0.35:
            risk_level = 'MEDIUM'
        else:
            risk_level = 'LOW'

        # Estimate confidence based on data completeness
        confidence = 0.7
        if changed_files > 0 and changed_lines > 0:
            confidence = 0.8

        return {
            'risk_level': risk_level,
            'confidence': confidence,
            'risk_score': risk_score,
            'probabilities': self._estimate_probabilities(risk_score)
        }

    def _estimate_probabilities(self, risk_score: float) -> Dict[str, float]:
        """Estimate probability distribution from risk score"""
        # Simple distribution estimation
        if risk_score > 0.75:
            return {'LOW': 0.05, 'MEDIUM': 0.1, 'HIGH': 0.25, 'CRITICAL': 0.6}
        elif risk_score > 0.55:
            return {'LOW': 0.1, 'MEDIUM': 0.2, 'HIGH': 0.5, 'CRITICAL': 0.2}
        elif risk_score > 0.35:
            return {'LOW': 0.2, 'MEDIUM': 0.5, 'HIGH': 0.25, 'CRITICAL': 0.05}
        else:
            return {'LOW': 0.6, 'MEDIUM': 0.3, 'HIGH': 0.08, 'CRITICAL': 0.02}

    def _analyze_risk_factors(self, code_change: Dict[str, Any]) -> List[Dict[str, Any]]:
        """Analyze specific risk factors"""
        factors = []

        # Check file count
        file_count = code_change.get('changed_files_count', 0)
        if file_count > 10:
            factors.append({
                'factor': 'large_change_scope',
                'severity': 'HIGH',
                'description': f'变更涉及 {file_count} 个文件，范围较大'
            })

        # Check critical modules
        critical_modules = ['payment', 'auth', 'security', 'core', 'database']
        changed_modules = code_change.get('changed_modules', [])
        for module in changed_modules:
            if any(critical in module.lower() for critical in critical_modules):
                factors.append({
                    'factor': 'critical_module',
                    'severity': 'CRITICAL',
                    'description': f'变更涉及关键模块: {module}'
                })

        # Check change type
        change_type = code_change.get('change_type', 'feature')
        if change_type == 'hotfix':
            factors.append({
                'factor': 'hotfix',
                'severity': 'HIGH',
                'description': '紧急修复，需要快速验证'
            })

        # Check complexity
        complexity = code_change.get('code_complexity_delta', 0)
        if complexity > 5:
            factors.append({
                'factor': 'increased_complexity',
                'severity': 'MEDIUM',
                'description': f'代码复杂度增加 {complexity:.1f}'
            })

        return factors

    def _suggest_mitigations(self, risk_level: str) -> List[str]:
        """Suggest risk mitigation strategies"""
        suggestions = {
            'CRITICAL': [
                '建议进行全面测试 (FULL scope)',
                '建议代码审查由高级工程师执行',
                '建议在STAGING环境充分验证后再上线',
                '建议准备回滚方案',
                '建议监控上线后的关键指标'
            ],
            'HIGH': [
                '建议进行核心功能测试 (CORE scope)',
                '建议增加相关模块的代码审查',
                '建议在STAGING环境验证',
                '建议准备回滚计划'
            ],
            'MEDIUM': [
                '建议进行核心功能测试',
                '建议关注相关功能的回归测试',
                '建议监控关键业务指标'
            ],
            'LOW': [
                '建议进行冒烟测试 (SMOKE scope)',
                '建议正常代码审查流程'
            ]
        }

        return suggestions.get(risk_level, suggestions['MEDIUM'])


class EnvironmentRecommender:
    """
    Recommends optimal testing environment

    Uses collaborative filtering + multi-factor scoring
    """

    def __init__(self):
        self.weights = {
            'availability': 0.30,
            'stability': 0.25,
            'performance': 0.20,
            'historical_success': 0.15,
            'load_balance': 0.10
        }

    def recommend(self, requirements: Dict[str, Any]) -> List[Dict[str, Any]]:
        """
        Recommend environments based on requirements

        Args:
            requirements: Environment requirements

        Returns:
            Ranked list of environment recommendations
        """
        environments = self._get_available_environments()
        scored_envs = []

        for env in environments:
            score = self._calculate_environment_score(env, requirements)
            scored_envs.append({
                'name': env['name'],
                'score': score,
                'status': env['status'],
                'recommendation_reason': self._generate_reason(env, score)
            })

        # Sort by score descending
        scored_envs.sort(key=lambda x: x['score'], reverse=True)

        return scored_envs

    def _get_available_environments(self) -> List[Dict[str, Any]]:
        """Get list of available environments"""
        # In production, this would fetch from infrastructure service
        return [
            {
                'name': 'DEV',
                'status': {
                    'available': True,
                    'stability_score': 0.90,
                    'performance_score': 0.85,
                    'historical_success_rate': 0.92,
                    'current_load': 0.3
                }
            },
            {
                'name': 'STAGING',
                'status': {
                    'available': True,
                    'stability_score': 0.95,
                    'performance_score': 0.90,
                    'historical_success_rate': 0.96,
                    'current_load': 0.5
                }
            },
            {
                'name': 'PROD',
                'status': {
                    'available': True,
                    'stability_score': 0.99,
                    'performance_score': 0.95,
                    'historical_success_rate': 0.98,
                    'current_load': 0.7
                }
            }
        ]

    def _calculate_environment_score(
        self,
        env: Dict[str, Any],
        requirements: Dict[str, Any]
    ) -> float:
        """Calculate score for environment"""
        status = env['status']

        if not status.get('available', False):
            return 0.0

        score = (
            self.weights['availability'] * 1.0 +
            self.weights['stability'] * status.get('stability_score', 0.9) +
            self.weights['performance'] * status.get('performance_score', 0.8) +
            self.weights['historical_success'] * status.get('historical_success_rate', 0.9) +
            self.weights['load_balance'] * (1 - status.get('current_load', 0.5))
        )

        return round(score, 3)

    def _generate_reason(self, env: Dict[str, Any], score: float) -> str:
        """Generate recommendation reason"""
        status = env['status']
        reasons = []

        if status.get('stability_score', 0) > 0.95:
            reasons.append('稳定性高')
        if status.get('current_load', 1) < 0.4:
            reasons.append('负载低')
        if status.get('historical_success_rate', 0) > 0.95:
            reasons.append('历史成功率高')

        if reasons:
            return ', '.join(reasons)
        return f'综合评分 {score:.2f}'
