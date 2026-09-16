package com.example.grupo_1_gameeducator.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

// Value Object: encapsula e valida o nome do aluno.
// @Embeddable permite embutir este objeto dentro da tabela da entidade.
@Embeddable
public class NomeAluno {

    // O nome da coluna e definido aqui, senao todos os VOs virariam "valor".
    @Column(name = "nome", nullable = false)
    private String valor;

    // protected e suficiente para o JPA e evita uso indevido fora do dominio.
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
