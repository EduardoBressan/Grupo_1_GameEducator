package com.example.grupo_1_gameeducator.domain;

// Camada: DOMINIO (TDD).
// A area do curso e usada para oferecer cursos relacionados no resgate.
public class Curso {

    private static final String AREA_PADRAO = "Geral";

    private final String nome;
    private final String area;

    public Curso(String nome, String area) {
        this.nome = nome;
        this.area = area;
    }

    public Curso(String nome) {
        this(nome, AREA_PADRAO);
    }

    public String getNome() {
        return nome;
    }

    public String getArea() {
        return area;
    }
}
