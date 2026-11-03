"""Mede as dependencias entre pacotes do Core (evidencia do Desafio 1).

Uso, na raiz do projeto:
    python docs/arquitetura/evidencias/desafio1/medir_dependencias.py

Conta apenas imports do proprio projeto (com.example.grupo_1_gameeducator.*)
em src/main/java. Uma "dependencia entre pacotes" e um par distinto
pacote_origem -> pacote_destino, com origem diferente de destino.
"""

import re
import sys
from pathlib import Path

RAIZ = Path(sys.argv[1]) if len(sys.argv) > 1 else Path(".")
FONTES = RAIZ / "src" / "main" / "java"
BASE = "com.example.grupo_1_gameeducator"

# Referencias a regra de gamificacao (media minima, +3 cursos, limite de 10 opcoes).
REGRA = re.compile(
    r"aprovadoParaCursosAdicionais|CURSOS_ADICIONAIS_POR_APROVACAO"
    r"|CURSOS_RELACIONADOS_OFERECIDOS|MEDIA_MINIMA|liberarCursosAdicionais"
)

IMPORT = re.compile(r"^import\s+(" + re.escape(BASE) + r"\.[\w.]+)\.(\w+);", re.M)
PACOTE = re.compile(r"^package\s+([\w.]+);", re.M)


def curto(pacote):
    return pacote.replace(BASE + ".", "").replace(BASE, "(raiz)")


def main():
    pares = set()
    por_classe = {}

    for arquivo in sorted(FONTES.rglob("*.java")):
        texto = arquivo.read_text(encoding="utf-8")
        origem = PACOTE.search(texto).group(1)
        imports = IMPORT.findall(texto)
        por_classe[arquivo.stem] = (origem, imports, len(REGRA.findall(texto)))
        for destino, _ in imports:
            if destino != origem:
                pares.add((curto(origem), curto(destino)))

    entre_classes = sum(
        1 for origem, imports, _ in por_classe.values()
        for destino, _ in imports if destino != origem
    )
    print(f"Imports entre classes de pacotes diferentes: {entre_classes}")
    print(f"Dependencias entre pacotes: {len(pares)}")
    for origem, destino in sorted(pares):
        print(f"  {origem} -> {destino}")

    print()
    origem, imports, regra = por_classe["MatriculaService"]
    print(f"MatriculaService: {len(imports)} imports do projeto, "
          f"{regra} referencias a regra de gamificacao")
    for destino, classe in imports:
        print(f"  {curto(destino)}.{classe}")


if __name__ == "__main__":
    main()
