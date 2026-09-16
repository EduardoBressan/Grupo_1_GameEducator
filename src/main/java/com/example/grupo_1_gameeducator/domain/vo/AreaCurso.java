package com.example.grupo_1_gameeducator.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class AreaCurso {

    public static final String AREA_PADRAO = "Geral";

    @Column(name = "area", nullable = false)
    private String valor;

    // So o JPA usa.
    protected AreaCurso() {
    }

    public AreaCurso(String valor) {
        this.valor = (valor == null || valor.isBlank()) ? AREA_PADRAO : valor.trim();
    }

    public String getValor() {
        return valor;
    }
}
