package com.example.grupo_1_gameeducator.recomendacoes;

import java.util.List;

// Desafio 3 (ADR-003): resultado da recomendacao, dizendo de onde ele veio.
public class Recomendacoes {

    public enum Origem {
        IA,
        INDISPONIVEL
    }

    private final Long alunoId;
    private final Origem origem;
    private final List<CursoRecomendado> cursos;

    private Recomendacoes(Long alunoId, Origem origem, List<CursoRecomendado> cursos) {
        this.alunoId = alunoId;
        this.origem = origem;
        this.cursos = cursos;
    }

    public static Recomendacoes daIa(Long alunoId, List<CursoRecomendado> cursos) {
        return new Recomendacoes(alunoId, Origem.IA, List.copyOf(cursos));
    }

    // Comportamento degradado: sem recomendacao personalizada, mas sem erro para quem chamou.
    public static Recomendacoes indisponiveis(Long alunoId) {
        return new Recomendacoes(alunoId, Origem.INDISPONIVEL, List.of());
    }

    public Long getAlunoId() {
        return alunoId;
    }

    public Origem getOrigem() {
        return origem;
    }

    public List<CursoRecomendado> getCursos() {
        return cursos;
    }

    public boolean isPersonalizadas() {
        return origem == Origem.IA;
    }
}
