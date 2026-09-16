package com.example.grupo_1_gameeducator.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

// Value Object: encapsula a descricao do curso.
// A descricao e opcional, entao este VO aceita valor nulo ou vazio.
@Embeddable
public class DescricaoCurso {

    @Column(name = "descricao", length = 500)
    private String valor;

    protected DescricaoCurso() {
    }

    public DescricaoCurso(String valor) {
        this.valor = (valor == null || valor.isBlank()) ? null : valor.trim();
    }

    public String getValor() {
        return valor;
    }
}
