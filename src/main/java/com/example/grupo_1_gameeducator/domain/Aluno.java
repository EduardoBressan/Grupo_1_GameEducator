package com.example.grupo_1_gameeducator.domain;

import com.example.grupo_1_gameeducator.domain.vo.EmailAluno;
import com.example.grupo_1_gameeducator.domain.vo.NomeAluno;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "alunos")
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private NomeAluno nome;

    @Embedded
    private EmailAluno email;

    // Quantos cursos extras o aluno tem direito de fazer.
    @Column(nullable = false)
    private Integer cursosAdicionaisDisponiveis;

    // So o JPA usa.
    protected Aluno() {
    }

    public Aluno(String nome) {
        this.nome = new NomeAluno(nome);
        this.cursosAdicionaisDisponiveis = 0;
    }

    public Aluno(String nome, String email) {
        this(nome);
        this.email = new EmailAluno(email);
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome.getValor();
    }

    public String getEmail() {
        return email != null ? email.getValor() : null;
    }

    public Integer getCursosAdicionaisDisponiveis() {
        return cursosAdicionaisDisponiveis;
    }

    public void liberarCursosAdicionais(int quantidade) {
        if (quantidade < 0) {
            throw new IllegalArgumentException("Quantidade de cursos adicionais nao pode ser negativa.");
        }
        this.cursosAdicionaisDisponiveis += quantidade;
    }

    public void consumirCursoAdicional() {
        if (this.cursosAdicionaisDisponiveis <= 0) {
            throw new IllegalStateException("Aluno sem cursos adicionais disponiveis.");
        }
        this.cursosAdicionaisDisponiveis--;
    }

    public void alterarNome(String nome) {
        this.nome = new NomeAluno(nome);
    }

    public void alterarEmail(String email) {
        this.email = new EmailAluno(email);
    }
}
