package com.example.grupo_1_gameeducator.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "matriculas")
public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id")
    private Curso curso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusMatricula status;

    @Column
    private Double mediaFinal;

    // true = o aluno esta usando um dos cursos adicionais que conquistou.
    @Column(nullable = false)
    private boolean cursoAdicional;

    // So o JPA usa.
    protected Matricula() {
    }

    public Matricula(Aluno aluno, Curso curso) {
        this(aluno, curso, false);
    }

    public Matricula(Aluno aluno, Curso curso, boolean cursoAdicional) {
        this.aluno = aluno;
        this.curso = curso;
        this.cursoAdicional = cursoAdicional;
        this.status = StatusMatricula.EM_ANDAMENTO;
    }

    public Long getId() {
        return id;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public Curso getCurso() {
        return curso;
    }

    public StatusMatricula getStatus() {
        return status;
    }

    public Double getMediaFinal() {
        return mediaFinal;
    }

    public boolean isCursoAdicional() {
        return cursoAdicional;
    }

    public void concluirCom(Double mediaFinal) {
        this.status = StatusMatricula.CONCLUIDO;
        this.mediaFinal = mediaFinal;
    }
}
