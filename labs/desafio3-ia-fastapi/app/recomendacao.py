"""Modelo de recomendacao do lab (Desafio 3).

E um modelo de regras deterministico, so para o lab: o objetivo do Desafio 3
e testar a fronteira entre o Core e o servico Python, nao a qualidade do modelo.
Um modelo treinado entraria aqui sem mudar o contrato HTTP.
"""

CATALOGO = [
    {"titulo": "Estruturas de Dados", "area": "Programacao"},
    {"titulo": "Algoritmos Avancados", "area": "Programacao"},
    {"titulo": "Testes Automatizados", "area": "Programacao"},
    {"titulo": "Arquitetura de Software", "area": "Programacao"},
    {"titulo": "DevOps Essencial", "area": "Programacao"},
    {"titulo": "Fundamentos de Design", "area": "Design"},
    {"titulo": "Design de Interfaces", "area": "Design"},
    {"titulo": "Marketing Digital", "area": "Marketing"},
]

MOTIVO = "Alunos com historico parecido tambem fizeram este curso"


def recomendar(aluno_id: int, limite: int = 3) -> list[dict]:
    """Escolhe `limite` cursos do catalogo, de forma estavel para cada aluno."""
    limite = max(0, min(limite, len(CATALOGO)))
    inicio = aluno_id % len(CATALOGO)
    escolhidos = [CATALOGO[(inicio + i) % len(CATALOGO)] for i in range(limite)]
    return [{**curso, "motivo": MOTIVO} for curso in escolhidos]
