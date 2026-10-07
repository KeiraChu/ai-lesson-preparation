.PHONY: up down test test-ai test-backend test-frontend eval
up:
	docker compose up --build
down:
	docker compose down
test: test-ai test-backend test-frontend
test-ai:
	cd ai-service && PYTHONPATH=. pytest -q
test-backend:
	cd backend && mvn test
test-frontend:
	cd frontend && npm run build
eval:
	cd ai-service && PYTHONPATH=. python run_evals.py ../evals/datasets/lesson_plan_cases.jsonl
