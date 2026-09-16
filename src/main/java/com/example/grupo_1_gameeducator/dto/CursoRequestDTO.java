package com.example.grupo_1_gameeducator.dto;

import jakarta.validation.constraints.NotBlank;

// Camada: DTO. Entrada da API para criacao de curso.
public class CursoRequestDTO {

    @NotBlank(message = "Titulo e obrigatorio")
    private String titulo;

    private String descricao;

    // Cenario do Felipe: usada para oferecer cursos relacionados no resgate.
    // Opcional: se nao informada, o service usa "Geral".
    private String area;

    public CursoRequestDTO() {
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
