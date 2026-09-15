package com.example.grupo_1_gameeducator.domain;

// Camada: DOMINIO (TDD).
// Guarda o vinculo aluno-curso e o resultado obtido.
public class Matricula {

    private final Aluno aluno;
    private final Curso curso;
    private SituacaoMatricula situacao;
    private double media;

    public Matricula(Aluno aluno, Curso curso) {
        this.aluno = aluno;
        this.curso = curso;
        this.situacao = SituacaoMatricula.EM_ANDAMENTO;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public Curso getCurso() {
        return curso;
    }

    public SituacaoMatricula getSituacao() {
        return situacao;
    }

    public double getMedia() {
        return media;
    }

    public void registrarResultado(double media, SituacaoMatricula situacao) {
        this.media = media;
        this.situacao = situacao;
    }
}
