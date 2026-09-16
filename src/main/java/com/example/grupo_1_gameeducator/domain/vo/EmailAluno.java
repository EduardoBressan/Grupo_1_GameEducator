package com.example.grupo_1_gameeducator.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class EmailAluno {

    @Column(name = "email", unique = true)
    private String valor;

    // So o JPA usa.
    protected EmailAluno() {
    }

    public EmailAluno(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("E-mail do aluno e obrigatorio.");
        }

        String normalizado = valor.trim().toLowerCase();

        if (!normalizado.contains("@")) {
            throw new IllegalArgumentException("E-mail invalido: " + valor);
        }

        this.valor = normalizado;
    }

    public String getValor() {
        return valor;
    }
}
