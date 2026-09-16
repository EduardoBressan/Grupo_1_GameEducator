package com.example.grupo_1_gameeducator.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Camada: DOMINIO (TDD).
//
// US escolhida pelo grupo (ADM, redigida por Luiza Bottesi):
// COMO administrador da plataforma, QUERO que o aluno, ao terminar um curso com
// media acima de 7,0, tenha direito a realizacao de mais 3 cursos, PARA fidelizar
// alunos a longo prazo e garantir qualidade de ensino.
//
// Esta classe concentra os quatro cenarios BDD escritos pelo grupo:
//  - Luiza Bottesi: media acima de 7,0 libera 3 cursos adicionais
//  - Joao: media exatamente 7,0 nao libera nada
//  - Eduardo Bressan: depois de usar os 3, uma nova aprovacao concede mais 3
//  - Felipe Rondello: no resgate, o aluno escolhe 3 entre 10 cursos relacionados
public class PlataformaEnsino {

    public static final int CURSOS_ADICIONAIS_POR_APROVACAO = 3;
    public static final double MEDIA_MINIMA = 7.0;
    public static final int CURSOS_RELACIONADOS_OFERECIDOS = 10;

    private final List<Curso> catalogoDeCursos;

    // Saldo de cursos adicionais que cada aluno tem direito de fazer.
    private final Map<Aluno, Integer> cursosAdicionaisPorAluno = new HashMap<>();

    // Ultimo curso concluido por aluno, usado para montar a oferta de relacionados.
    private final Map<Aluno, Curso> ultimoCursoConcluido = new HashMap<>();

    public PlataformaEnsino() {
        this(catalogoPadrao());
    }

    public PlataformaEnsino(List<Curso> catalogoDeCursos) {
        this.catalogoDeCursos = catalogoDeCursos;
    }

    public Matricula matricular(Aluno aluno, Curso curso) {
        return new Matricula(aluno, curso);
    }

    public void finalizarCurso(Matricula matricula, double media) {
        matricula.registrarResultado(media, SituacaoMatricula.CONCLUIDA);

        Aluno aluno = matricula.getAluno();
        ultimoCursoConcluido.put(aluno, matricula.getCurso());

        if (aprovadoParaCursosAdicionais(media)) {
            int saldoAtual = cursosAdicionaisLiberadosPara(aluno);
            cursosAdicionaisPorAluno.put(aluno, saldoAtual + CURSOS_ADICIONAIS_POR_APROVACAO);
        }
    }

    public int cursosAdicionaisLiberadosPara(Aluno aluno) {
        return cursosAdicionaisPorAluno.getOrDefault(aluno, 0);
    }

    // Cenario do Eduardo: o aluno gasta os cursos adicionais que conquistou.
    // Zerar o saldo aqui e o que permite provar que uma nova aprovacao concede mais 3.
    public void usarCursosAdicionais(Aluno aluno, int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade a usar deve ser maior que zero.");
        }

        int saldoAtual = cursosAdicionaisLiberadosPara(aluno);
        if (quantidade > saldoAtual) {
            throw new IllegalStateException("Aluno nao tem cursos adicionais suficientes.");
        }

        cursosAdicionaisPorAluno.put(aluno, saldoAtual - quantidade);
    }

    // Cenario do Felipe: ao resgatar, o aluno recebe 10 cursos da mesma area do
    // que concluiu e pode escolher 3 deles.
    public Resgate iniciarResgate(Aluno aluno) {
        if (cursosAdicionaisLiberadosPara(aluno) <= 0) {
            throw new IllegalStateException("Aluno nao tem cursos adicionais para resgatar.");
        }

        Curso cursoConcluido = ultimoCursoConcluido.get(aluno);

        List<Curso> relacionados = catalogoDeCursos.stream()
                .filter(curso -> curso.getArea().equals(cursoConcluido.getArea()))
                .filter(curso -> !curso.getNome().equals(cursoConcluido.getNome()))
                .limit(CURSOS_RELACIONADOS_OFERECIDOS)
                .toList();

        return new Resgate(relacionados, CURSOS_ADICIONAIS_POR_APROVACAO);
    }

    // A media precisa ser ACIMA de 7,0. Exatamente 7,0 nao libera nada.
    private boolean aprovadoParaCursosAdicionais(double media) {
        return media > MEDIA_MINIMA;
    }

    private static List<Curso> catalogoPadrao() {
        return List.of(
                new Curso("Estruturas de Dados", "Programacao"),
                new Curso("Algoritmos Avancados", "Programacao"),
                new Curso("Programacao Orientada a Objetos", "Programacao"),
                new Curso("Programacao Funcional", "Programacao"),
                new Curso("Desenvolvimento Web", "Programacao"),
                new Curso("Desenvolvimento Mobile", "Programacao"),
                new Curso("Banco de Dados", "Programacao"),
                new Curso("Testes Automatizados", "Programacao"),
                new Curso("Arquitetura de Software", "Programacao"),
                new Curso("DevOps Essencial", "Programacao"),
                new Curso("Fundamentos de Design", "Design"),
                new Curso("Marketing Digital", "Marketing")
        );
    }
}
