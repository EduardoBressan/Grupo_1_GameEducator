package com.example.grupo_1_gameeducator.conquistas;

import com.example.grupo_1_gameeducator.domain.Curso;

import java.util.List;

// Desafio 2 (ADR-002): porta de saida do modulo Conquistas.
// O caso de uso precisa dos cursos relacionados ao que o aluno concluiu,
// mas nao precisa saber de onde eles vem (JPA, memoria, outro servico).
// Quem implementa sao os adapters, fora do nucleo do modulo.
public interface CatalogoDeCursos {

    // Cursos da mesma area do curso concluido, sem incluir o proprio curso.
    List<Curso> relacionadosA(Curso cursoConcluido);
}
