package com.example.grupo_1_gameeducator.domain;

import java.util.List;

// Camada: DOMINIO (TDD).
// Representa a oferta de cursos relacionados que o aluno recebe ao resgatar
// os cursos adicionais conquistados: uma lista de opcoes e um limite de escolhas.
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
