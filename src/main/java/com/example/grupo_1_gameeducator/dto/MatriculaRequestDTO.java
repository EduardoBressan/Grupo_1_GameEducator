package com.example.grupo_1_gameeducator.dto;

import jakarta.validation.constraints.NotNull;

// Camada: DTO. Entrada da API para matricular um aluno em um curso.
public class MatriculaRequestDTO {

    @NotNull(message = "alunoId e obrigatorio")
    private Long alunoId;

    @NotNull(message = "cursoId e obrigatorio")
    private Long cursoId;

    // true = o aluno esta usando um dos cursos adicionais conquistados.
    private boolean cursoAdicional;

    public MatriculaRequestDTO() {
    }

    public Long getAlunoId() {
        return alunoId;
    }

    public void setAlunoId(Long alunoId) {
        this.alunoId = alunoId;
    }

    public Long getCursoId() {
        return cursoId;
    }

    public void setCursoId(Long cursoId) {
        this.cursoId = cursoId;
    }

    public boolean isCursoAdicional() {
        return cursoAdicional;
    }

    public void setCursoAdicional(boolean cursoAdicional) {
        this.cursoAdicional = cursoAdicional;
    }
}
