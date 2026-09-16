package com.example.grupo_1_gameeducator.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

// Descricao e opcional.
@Embeddable
public class DescricaoCurso {

    @Column(name = "descricao", length = 500)
    private String valor;

    // So o JPA usa.
    protected DescricaoCurso() {
    }

    public DescricaoCurso(String valor) {
        this.valor = (valor == null || valor.isBlank()) ? null : valor.trim();
    }

    public String getValor() {
        return valor;
    }
}
