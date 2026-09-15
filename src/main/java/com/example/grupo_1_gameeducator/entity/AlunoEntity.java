package com.example.grupo_1_gameeducator.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Camada: ENTITY.
// Representacao do aluno no banco. E diferente da classe de dominio
// com.example.grupo_1_gameeducator.domain.Aluno, que existe para o TDD.
//
// O saldo de cursos adicionais fica aqui porque e exatamente o que a US
// escolhida controla: quantos cursos extras o aluno tem direito de fazer.
@Entity
@Table(name = "alunos")
public class AlunoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private Integer cursosAdicionaisDisponiveis;

    protected AlunoEntity() {
    }

    public AlunoEntity(String nome, String email) {
        this.nome = nome;
        this.email = email;
        this.cursosAdicionaisDisponiveis = 0;
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

    // Credita os cursos adicionais liberados por uma conclusao aprovada.
    public void liberarCursosAdicionais(int quantidade) {
        if (quantidade < 0) {
            throw new IllegalArgumentException("Quantidade de cursos adicionais nao pode ser negativa.");
        }
        this.cursosAdicionaisDisponiveis += quantidade;
    }

    // Consome um curso adicional quando o aluno usa o beneficio.
    public void consumirCursoAdicional() {
        if (this.cursosAdicionaisDisponiveis <= 0) {
            throw new IllegalStateException("Aluno sem cursos adicionais disponiveis.");
        }
        this.cursosAdicionaisDisponiveis--;
    }
}
