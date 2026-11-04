package com.example.grupo_1_gameeducator.recomendacoes;

// Desafio 3 (ADR-003): curso sugerido pelo servico de IA.
public class CursoRecomendado {

    private final String titulo;
    private final String area;
    private final String motivo;

    public CursoRecomendado(String titulo, String area, String motivo) {
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
