# Lab do Desafio 3 — Serviço de IA (FastAPI)

Serviço Python mínimo usado como evidência do [ADR-003](../../docs/arquitetura/README.md#adr-003--serviço-python-de-ia-fora-do-core-com-degradação).
O Core Java chama este serviço por HTTP/JSON (`GET /recomendacoes/{alunoId}`) com timeout de 2 s e, se ele estiver fora do ar ou lento, responde sem recomendações em vez de falhar.

O "modelo" é um conjunto de regras determinístico (`app/recomendacao.py`): o objetivo do lab é testar a fronteira entre o Core e a IA, não a qualidade da recomendação.

## Endpoints

| Método | Rota | Resposta |
|---|---|---|
| GET | `/health` | `{"status": "ok", "modelo": "regras-v1"}` |
| GET | `/recomendacoes/{alunoId}?limite=3` | `{"alunoId": 7, "modelo": "regras-v1", "cursos": [{"titulo", "area", "motivo"}]}` |
| GET | `/docs` | Swagger do FastAPI |

## Rodar

Com Docker (junto com o Core e o PostgreSQL), na raiz do repositório:

```
docker compose up -d --build
```

Sem Docker, nesta pasta:

```
pip install -r requirements-dev.txt
uvicorn app.main:app --port 8000
```

## Testes

```
pip install -r requirements-dev.txt
python -m pytest -v
```

## Simular falhas

| Cenário | Como provocar |
|---|---|
| IA fora do ar | `docker compose stop ia` (ou Ctrl+C no uvicorn) |
| IA lenta | `IA_ATRASO_SEGUNDOS=5` no ambiente do serviço (no compose: `docker compose run` ou editar o `environment` do serviço `ia`) |

O roteiro completo do mini-lab, com os comandos e o que registrar, está em [`docs/arquitetura/evidencias/desafio3/`](../../docs/arquitetura/evidencias/desafio3).
