package com.example.grupo_1_gameeducator.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// Camada: DTO. Entrada da API para criacao de aluno.
public class AlunoRequestDTO {

    @NotBlank(message = "Nome e obrigatorio")
    private String nome;

    @Email(message = "E-mail invalido")
    @NotBlank(message = "E-mail e obrigatorio")
    private String email;

    public AlunoRequestDTO() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
