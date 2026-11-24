from fastapi.testclient import TestClient

from naengo_ai.main import app


def test_상태_확인은_ok를_돌려준다():
    response = TestClient(app).get("/health")

    assert response.status_code == 200
    assert response.json() == {"status": "ok"}
