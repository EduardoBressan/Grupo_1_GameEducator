from fastapi.testclient import TestClient

from app.main import app

cliente = TestClient(app)


def test_health_responde_ok():
    resposta = cliente.get("/health")

    assert resposta.status_code == 200
    assert resposta.json()["status"] == "ok"


def test_recomenda_tres_cursos_por_padrao_no_contrato_esperado_pelo_core():
    resposta = cliente.get("/recomendacoes/7")

    corpo = resposta.json()
    assert resposta.status_code == 200
    assert corpo["alunoId"] == 7
    assert len(corpo["cursos"]) == 3
    for curso in corpo["cursos"]:
        assert set(curso) == {"titulo", "area", "motivo"}


def test_recomendacao_e_estavel_para_o_mesmo_aluno():
    primeira = cliente.get("/recomendacoes/3").json()["cursos"]
    segunda = cliente.get("/recomendacoes/3").json()["cursos"]

    assert primeira == segunda


def test_respeita_o_limite_pedido():
    resposta = cliente.get("/recomendacoes/1", params={"limite": 5})

    assert len(resposta.json()["cursos"]) == 5


def test_rejeita_limite_fora_da_faixa():
    resposta = cliente.get("/recomendacoes/1", params={"limite": 50})

    assert resposta.status_code == 422
