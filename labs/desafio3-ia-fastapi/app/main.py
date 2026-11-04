"""Servico de IA do GameEducator (Desafio 3 do B1, ADR-003).

Fronteira propria, fora do Core Java: o Core chama este servico por HTTP/JSON
com timeout e tem fallback se ele estiver fora do ar ou lento.

Rodar:  uvicorn app.main:app --port 8000
Simular modelo lento:  IA_ATRASO_SEGUNDOS=5 uvicorn app.main:app --port 8000
"""

import os
import time

from fastapi import FastAPI, Query

from app.recomendacao import recomendar

MODELO = "regras-v1"

app = FastAPI(
    title="GameEducator IA",
    version="0.1.0",
    description="Recomendacao de cursos (ML). O assistente RAG/LLM entra neste mesmo servico, em outro modulo.",
)


def _simular_atraso() -> None:
    segundos = float(os.getenv("IA_ATRASO_SEGUNDOS", "0"))
    if segundos > 0:
        time.sleep(segundos)


@app.get("/health")
def health() -> dict:
    return {"status": "ok", "modelo": MODELO}


@app.get("/recomendacoes/{aluno_id}")
def recomendacoes(aluno_id: int, limite: int = Query(default=3, ge=1, le=10)) -> dict:
    _simular_atraso()
    return {"alunoId": aluno_id, "modelo": MODELO, "cursos": recomendar(aluno_id, limite)}
