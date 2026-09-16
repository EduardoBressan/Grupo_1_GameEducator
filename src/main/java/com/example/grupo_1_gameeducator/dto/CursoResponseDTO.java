package com.example.grupo_1_gameeducator.dto;

// Camada: DTO. Saida da API para curso.
public class CursoResponseDTO {

    private Long id;
    private String titulo;
    private String descricao;
    private String area;

    public CursoResponseDTO() {
    }

    public CursoResponseDTO(Long id, String titulo, String descricao, String area) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.area = area;
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getArea() {
        return area;
    }
}
