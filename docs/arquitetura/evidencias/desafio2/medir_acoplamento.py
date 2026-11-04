"""Mede o acoplamento do modulo Conquistas e do dominio com a infraestrutura (evidencia do Desafio 2).

Uso, na raiz do projeto:
    python docs/arquitetura/evidencias/desafio2/medir_acoplamento.py

Conta, em src/main/java, os imports de infraestrutura de cada classe:
repositorios JPA do projeto, Spring Data e jakarta.persistence.
Separa o nucleo do modulo Conquistas (pacote conquistas), os adapters
(conquistas.adapter.*) e o dominio (domain e domain.vo).
"""

import re
import sys
from pathlib import Path

RAIZ = Path(sys.argv[1]) if len(sys.argv) > 1 else Path(".")
FONTES = RAIZ / "src" / "main" / "java"
BASE = "com.example.grupo_1_gameeducator"

INFRAESTRUTURA = {
    "repositorio JPA do projeto": re.compile(r"^import\s+" + re.escape(BASE) + r"\.repository\.(\w+);", re.M),
    "Spring Data": re.compile(r"^import\s+org\.springframework\.data\.[\w.]+\.(\w+);", re.M),
    "JPA (jakarta.persistence)": re.compile(r"^import\s+jakarta\.persistence\.(\w+);", re.M),
}
PACOTE = re.compile(r"^package\s+([\w.]+);", re.M)

GRUPOS = [
    ("Nucleo do modulo Conquistas (pacote conquistas)", lambda p: p == BASE + ".conquistas"),
    ("Adapters do modulo (conquistas.adapter.*)", lambda p: p.startswith(BASE + ".conquistas.adapter")),
    ("Dominio (domain e domain.vo)", lambda p: p == BASE + ".domain" or p.startswith(BASE + ".domain.")),
]


def main():
    classes = []
    for arquivo in sorted(FONTES.rglob("*.java")):
        texto = arquivo.read_text(encoding="utf-8")
        pacote = PACOTE.search(texto).group(1)
        encontrados = {nome: regex.findall(texto) for nome, regex in INFRAESTRUTURA.items()}
        classes.append((pacote, arquivo.stem, encontrados))

    for titulo, pertence in GRUPOS:
        do_grupo = [c for c in classes if pertence(c[0])]
        print(f"{titulo}: {len(do_grupo)} classes")
        for nome in INFRAESTRUTURA:
            total = sum(len(enc[nome]) for _, _, enc in do_grupo)
            com = [classe for _, classe, enc in do_grupo if enc[nome]]
            print(f"  imports de {nome}: {total}" + (f" ({', '.join(com)})" if com else ""))
        print()

    print("Classes do nucleo do modulo e o que recebem de infraestrutura:")
    for pacote, classe, enc in classes:
        if pacote == BASE + ".conquistas":
            usados = [f"{n}: {', '.join(v)}" for n, v in enc.items() if v]
            print(f"  {classe}: {'; '.join(usados) if usados else 'nenhum'}")


if __name__ == "__main__":
    main()
