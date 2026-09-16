package com.example.grupo_1_gameeducator.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class NomeAluno {

    @Column(name = "nome", nullable = false)
    private String valor;

    // So o JPA usa.
    protected NomeAluno() {
    }

    public NomeAluno(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Nome do aluno e obrigatorio.");
        }
        this.valor = valor.trim();
    }

    public String getValor() {
        return valor;
    }
}
