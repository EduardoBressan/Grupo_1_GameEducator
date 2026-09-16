package com.example.grupo_1_gameeducator.dto;

import java.util.List;

// Camada: DTO. Saida da API para o resgate de cursos adicionais (cenario do Felipe).
public class ResgateResponseDTO {

    private List<CursoResponseDTO> cursosDisponiveis;
    private int limiteDeEscolhas;

    public ResgateResponseDTO() {
    }

    public ResgateResponseDTO(List<CursoResponseDTO> cursosDisponiveis, int limiteDeEscolhas) {
        this.cursosDisponiveis = cursosDisponiveis;
        this.limiteDeEscolhas = limiteDeEscolhas;
    }

    public List<CursoResponseDTO> getCursosDisponiveis() {
        return cursosDisponiveis;
    }

    public int getLimiteDeEscolhas() {
        return limiteDeEscolhas;
    }
}
