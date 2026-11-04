package com.example.grupo_1_gameeducator.dto;

// Camada: DTO. Curso sugerido pelo servico de IA (Desafio 3).
public class CursoRecomendadoDTO {

    private String titulo;
    private String area;
    private String motivo;

    public CursoRecomendadoDTO() {
    }

    public CursoRecomendadoDTO(String titulo, String area, String motivo) {
        this.titulo = titulo;
        this.area = area;
        this.motivo = motivo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getArea() {
        return area;
    }

    public String getMotivo() {
        return motivo;
    }
}
