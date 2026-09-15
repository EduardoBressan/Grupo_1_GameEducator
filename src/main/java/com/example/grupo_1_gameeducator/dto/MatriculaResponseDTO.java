package com.example.grupo_1_gameeducator.dto;

// Camada: DTO. Saida da API para matricula.
public class MatriculaResponseDTO {

    private Long id;
    private Long alunoId;
    private String alunoNome;
    private Long cursoId;
    private String cursoTitulo;
    private String status;
    private Double mediaFinal;
    private boolean cursoAdicional;
    private Integer cursosAdicionaisLiberados;
    private Integer cursosAdicionaisDisponiveis;

    public MatriculaResponseDTO() {
    }

    public MatriculaResponseDTO(Long id, Long alunoId, String alunoNome, Long cursoId, String cursoTitulo,
                                String status, Double mediaFinal, boolean cursoAdicional,
                                Integer cursosAdicionaisLiberados, Integer cursosAdicionaisDisponiveis) {
        this.id = id;
        this.alunoId = alunoId;
        this.alunoNome = alunoNome;
        this.cursoId = cursoId;
        this.cursoTitulo = cursoTitulo;
        this.status = status;
        this.mediaFinal = mediaFinal;
        this.cursoAdicional = cursoAdicional;
        this.cursosAdicionaisLiberados = cursosAdicionaisLiberados;
        this.cursosAdicionaisDisponiveis = cursosAdicionaisDisponiveis;
    }

    public Long getId() {
        return id;
    }

    public Long getAlunoId() {
        return alunoId;
    }

    public String getAlunoNome() {
        return alunoNome;
    }

    public Long getCursoId() {
        return cursoId;
    }

    public String getCursoTitulo() {
        return cursoTitulo;
    }

    public String getStatus() {
        return status;
    }

    public Double getMediaFinal() {
        return mediaFinal;
    }

    public boolean isCursoAdicional() {
        return cursoAdicional;
    }

    public Integer getCursosAdicionaisLiberados() {
        return cursosAdicionaisLiberados;
    }

    public Integer getCursosAdicionaisDisponiveis() {
        return cursosAdicionaisDisponiveis;
    }
}
