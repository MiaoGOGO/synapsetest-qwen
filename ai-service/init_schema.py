"""
MongoDB Schema Initialization for AI Training Data
"""
from pymongo import MongoClient, ASCENDING, DESCENDING
from datetime import datetime

def initialize_mongodb_schema():
    """Initialize MongoDB collections and indexes for AI training data"""

    client = MongoClient('mongodb://localhost:27017/')
    db = client['test_management_ai']

    # AI Models Collection
    ai_models = db['ai_models']
    ai_models.create_index([('name', ASCENDING), ('version', ASCENDING)], unique=True)
    ai_models.create_index([('security_status', ASCENDING)])
    ai_models.create_index([('created_at', DESCENDING)])

    # Training Data Collection
    training_data = db['training_data']
    training_data.create_index([('task_id', ASCENDING)])
    training_data.create_index([('created_at', DESCENDING)])
    training_data.create_index([('data_type', ASCENDING)])

    # Model Predictions Collection
    model_predictions = db['model_predictions']
    model_predictions.create_index([('model_id', ASCENDING)])
    model_predictions.create_index([('task_id', ASCENDING)])
    model_predictions.create_index([('created_at', DESCENDING)])

    # Feature Vectors Collection (for ML features)
    feature_vectors = db['feature_vectors']
    feature_vectors.create_index([('entity_id', ASCENDING)])
    feature_vectors.create_index([('entity_type', ASCENDING)])
    feature_vectors.create_index([('created_at', DESCENDING)])

    # Monitoring Metrics Collection
    monitoring_metrics = db['monitoring_metrics']
    monitoring_metrics.create_index([('service_name', ASCENDING)])
    monitoring_metrics.create_index([('metric_name', ASCENDING)])
    monitoring_metrics.create_index([('timestamp', DESCENDING)])
    monitoring_metrics.create_index([('alert_status', ASCENDING)])

    # Test Execution Logs Collection
    execution_logs = db['execution_logs']
    execution_logs.create_index([('task_id', ASCENDING)])
    execution_logs.create_index([('timestamp', DESCENDING)])
    execution_logs.create_index([('log_level', ASCENDING)])

    print("MongoDB schema initialized successfully")

    # Insert sample AI model document
    sample_ai_model = {
        "name": "test_recommendation_model",
        "version": "1.0.0",
        "description": "AI model for test recommendation",
        "file_path": "/models/test_recommendation_v1.pt",
        "security_status": "APPROVED",
        "vulnerability_scan_result": "PASS",
        "last_scan_time": datetime.utcnow(),
        "compliance_status": "COMPLIANT",
        "created_at": datetime.utcnow(),
        "updated_at": datetime.utcnow(),
        "metrics": {
            "accuracy": 0.85,
            "precision": 0.82,
            "recall": 0.88
        }
    }

    try:
        ai_models.insert_one(sample_ai_model)
        print("Sample AI model inserted")
    except Exception as e:
        print(f"Sample data already exists or error: {e}")

if __name__ == "__main__":
    initialize_mongodb_schema()
