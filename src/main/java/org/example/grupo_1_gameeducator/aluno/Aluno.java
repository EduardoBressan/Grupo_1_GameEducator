package org.example.grupo_1_gameeducator.aluno;

import java.util.ArrayList;
import java.util.List;

public class Aluno {
    private final String nome;
    private double mediaCurso;
    private final List<Curso> cursosAdicionaisConcedidos = new ArrayList<>();

    public Aluno(String nome, double mediaCurso) {
        this.nome = nome;
        this.mediaCurso = mediaCurso;
    }

    public double getMediaCurso() { return mediaCurso; }
    public void setMediaCurso(double mediaCurso) { this.mediaCurso = mediaCurso; }
    public List<Curso> getCursosAdicionaisConcedidos() { return cursosAdicionaisConcedidos; }
}