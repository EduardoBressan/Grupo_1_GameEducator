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

// Camada: DOMINIO.
// Segue o formato do projeto modelo da disciplina: a classe de dominio e a
// propria entidade persistida e usa Value Objects para encapsular os atributos.
//
// O saldo de cursos adicionais e o estado central da US escolhida pelo grupo:
// quantos cursos extras o aluno tem direito de fazer.
@Entity
@Table(name = "alunos")
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Embedded embute o Value Object dentro da tabela da entidade.
    @Embedded
    private NomeAluno nome;

    @Embedded
    private EmailAluno email;

    @Column(nullable = false)
    private Integer cursosAdicionaisDisponiveis;

    protected Aluno() {
    }

    // Usado pelos testes de dominio, onde o e-mail nao importa para a regra.
    public Aluno(String nome) {
        this.nome = new NomeAluno(nome);
        this.cursosAdicionaisDisponiveis = 0;
    }

    // Construtor rico: delega a validacao dos atributos para os Value Objects.
    public Aluno(String nome, String email) {
        this(nome);
        this.email = new EmailAluno(email);
    }

    public Long getId() {
        return id;
    }

    // A entidade expoe String para fora, mas internamente guarda os VOs.
    public String getNome() {
        return nome.getValor();
    }

    public String getEmail() {
        return email != null ? email.getValor() : null;
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

    // Alteracoes controladas do estado da entidade.
    public void alterarNome(String nome) {
        this.nome = new NomeAluno(nome);
    }

    public void alterarEmail(String email) {
        this.email = new EmailAluno(email);
    }
}
