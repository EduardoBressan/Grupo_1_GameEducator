package com.example.grupo_1_gameeducator.conquistas.adapter;

import com.example.grupo_1_gameeducator.conquistas.CatalogoDeCursos;
import com.example.grupo_1_gameeducator.conquistas.adapter.memoria.CatalogoDeCursosEmMemoria;
import com.example.grupo_1_gameeducator.domain.Curso;

// Desafio 2 (ADR-002): contrato da porta rodando contra o adapter em memoria (sem Spring nem banco).
class CatalogoDeCursosEmMemoriaTest extends CatalogoDeCursosContrato {

    private final CatalogoDeCursosEmMemoria catalogo = new CatalogoDeCursosEmMemoria();

    @Override
    protected CatalogoDeCursos catalogo() {
        return catalogo;
    }

    @Override
    protected Curso cadastrar(Curso curso) {
        catalogo.adicionar(curso);
        return curso;
    }
}
