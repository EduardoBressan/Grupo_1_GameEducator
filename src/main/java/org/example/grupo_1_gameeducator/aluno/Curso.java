package org.example.grupo_1_gameeducator.aluno;

public class Curso {
    private final String nome;
    private final String area;

    public Curso(String nome, String area) {
        this.nome = nome;
        this.area = area;
    }

    public String getNome() { return nome; }
    public String getArea() { return area; }
}