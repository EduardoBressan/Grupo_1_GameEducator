package org.example.grupo_1_gameeducator.aluno;

import java.util.List;

public class Resgate {

    private final List<Curso> cursosDisponiveis;
    private final int limiteDeEscolhas;

    public Resgate(List<Curso> cursosDisponiveis, int limiteDeEscolhas) {
        this.cursosDisponiveis = cursosDisponiveis;
        this.limiteDeEscolhas = limiteDeEscolhas;
    }

    public List<Curso> getCursosDisponiveis() {
        return cursosDisponiveis;
    }

    public int getLimiteDeEscolhas() {
        return limiteDeEscolhas;
    }
}
