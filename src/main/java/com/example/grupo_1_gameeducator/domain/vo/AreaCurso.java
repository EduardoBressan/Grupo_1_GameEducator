package com.example.grupo_1_gameeducator.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

// Value Object: encapsula a area do curso.
// A area e usada no resgate para oferecer cursos relacionados (cenario do Felipe).
// Quando nao informada, o proprio VO aplica a area padrao.
@Embeddable
public class AreaCurso {

    public static final String AREA_PADRAO = "Geral";

    @Column(name = "area", nullable = false)
    private String valor;

    protected AreaCurso() {
    }

    public AreaCurso(String valor) {
        this.valor = (valor == null || valor.isBlank()) ? AREA_PADRAO : valor.trim();
    }

    public String getValor() {
        return valor;
    }
}
