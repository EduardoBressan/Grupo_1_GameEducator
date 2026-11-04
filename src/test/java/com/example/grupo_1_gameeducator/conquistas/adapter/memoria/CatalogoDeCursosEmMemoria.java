package com.example.grupo_1_gameeducator.conquistas.adapter.memoria;

import com.example.grupo_1_gameeducator.conquistas.CatalogoDeCursos;
import com.example.grupo_1_gameeducator.domain.Curso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// Desafio 2 (ADR-002): adapter fake da porta CatalogoDeCursos, guardando os cursos numa lista.
// Substitui o adapter JPA nos testes do modulo: sem Spring, sem banco e sem Mockito.
public class CatalogoDeCursosEmMemoria implements CatalogoDeCursos {

    private final List<Curso> cursos = new ArrayList<>();

    public void adicionar(Curso curso) {
        cursos.add(curso);
    }

    @Override
    public List<Curso> relacionadosA(Curso cursoConcluido) {
        return cursos.stream()
                .filter(curso -> curso != cursoConcluido)
                .filter(curso -> Objects.equals(curso.getArea(), cursoConcluido.getArea()))
                .toList();
    }
}
