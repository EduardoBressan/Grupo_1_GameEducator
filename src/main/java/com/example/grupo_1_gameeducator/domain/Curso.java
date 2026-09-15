package com.example.grupo_1_gameeducator.domain;

// Camada: DOMINIO (TDD).
public class Curso {

    private final String nome;

    public Curso(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
}
