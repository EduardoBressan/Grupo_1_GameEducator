package com.example.grupo_1_gameeducator.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class TituloCurso {

    @Column(name = "titulo", nullable = false)
    private String valor;

    // So o JPA usa.
    protected TituloCurso() {
    }

    public TituloCurso(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Titulo do curso e obrigatorio.");
        }
        this.valor = valor.trim();
    }

    public String getValor() {
        return valor;
    }
}
