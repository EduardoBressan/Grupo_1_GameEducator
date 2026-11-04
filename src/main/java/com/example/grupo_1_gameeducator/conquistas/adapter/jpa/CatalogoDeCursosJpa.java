package com.example.grupo_1_gameeducator.conquistas.adapter.jpa;

import com.example.grupo_1_gameeducator.conquistas.CatalogoDeCursos;
import com.example.grupo_1_gameeducator.domain.Curso;
import com.example.grupo_1_gameeducator.repository.CursoRepository;
import org.springframework.stereotype.Component;

import java.util.List;

// Desafio 2 (ADR-002): adapter de saida que liga a porta CatalogoDeCursos ao banco via Spring Data JPA.
// E o unico ponto do modulo Conquistas que conhece o repositorio.
@Component
public class CatalogoDeCursosJpa implements CatalogoDeCursos {

    private final CursoRepository cursoRepository;

    public CatalogoDeCursosJpa(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    @Override
    public List<Curso> relacionadosA(Curso cursoConcluido) {
        return cursoRepository.findByArea_ValorAndIdNot(cursoConcluido.getArea(), cursoConcluido.getId());
    }
}
