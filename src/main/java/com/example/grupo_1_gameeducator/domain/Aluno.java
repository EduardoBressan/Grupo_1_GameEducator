package com.example.grupo_1_gameeducator.domain;

import java.util.Objects;

// Camada: DOMINIO (TDD).
// Classe de dominio pura: nao conhece Spring, nao conhece banco.
public class Aluno {

    private final String nome;

    public Aluno(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    // equals/hashCode para o aluno poder ser usado como chave de saldo de creditos.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Aluno outro)) {
            return false;
        }
        return Objects.equals(nome, outro.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome);
    }
}
