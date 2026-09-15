package com.example.grupo_1_gameeducator.dto;

// Camada: DTO. Saida da API para aluno.
public class AlunoResponseDTO {

    private Long id;
    private String nome;
    private String email;
    private Integer cursosAdicionaisDisponiveis;

    public AlunoResponseDTO() {
    }

    public AlunoResponseDTO(Long id, String nome, String email, Integer cursosAdicionaisDisponiveis) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.cursosAdicionaisDisponiveis = cursosAdicionaisDisponiveis;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public Integer getCursosAdicionaisDisponiveis() {
        return cursosAdicionaisDisponiveis;
    }
}
