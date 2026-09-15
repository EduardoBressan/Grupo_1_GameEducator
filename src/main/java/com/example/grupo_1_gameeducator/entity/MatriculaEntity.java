package com.example.grupo_1_gameeducator.entity;

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

// Camada: ENTITY.
@Entity
@Table(name = "matriculas")
public class MatriculaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id")
    private AlunoEntity aluno;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id")
    private CursoEntity curso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusMatricula status;

    @Column
    private Double mediaFinal;

    @Column(nullable = false)
    private boolean cursoAdicional;

    protected MatriculaEntity() {
    }

    // Toda matricula nova nasce EM_ANDAMENTO.
    public MatriculaEntity(AlunoEntity aluno, CursoEntity curso, boolean cursoAdicional) {
        this.aluno = aluno;
        this.curso = curso;
        this.cursoAdicional = cursoAdicional;
        this.status = StatusMatricula.EM_ANDAMENTO;
    }

    public Long getId() {
        return id;
    }

    public AlunoEntity getAluno() {
        return aluno;
    }

    public CursoEntity getCurso() {
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
