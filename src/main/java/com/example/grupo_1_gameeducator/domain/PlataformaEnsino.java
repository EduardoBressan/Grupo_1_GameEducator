package com.example.grupo_1_gameeducator.domain;

import java.util.List;

// Camada: DOMAIN (servico de dominio - onde vivem as regras construidas por TDD).
//
// US escolhida pelo grupo (ADM, redigida por Luiza Bottesi):
// COMO administrador da plataforma, QUERO que o aluno, ao terminar um curso com
// media acima de 7,0, tenha direito a realizacao de mais 3 cursos, PARA fidelizar
// alunos a longo prazo e garantir qualidade de ensino.
//
// Os quatro cenarios BDD do grupo:
//  - Luiza Bottesi: media acima de 7,0 libera 3 cursos adicionais
//  - Joao: media exatamente 7,0 nao torna o aluno elegivel
//  - Eduardo Bressan: depois de usar os 3, uma nova aprovacao concede mais 3
//  - Felipe Rondello: no resgate, o aluno escolhe 3 entre 10 cursos relacionados
//
// Esta classe nao guarda o saldo do aluno: quem guarda e a propria entidade
// Aluno, como no projeto modelo da disciplina.
public class PlataformaEnsino {

    public static final int CURSOS_ADICIONAIS_POR_APROVACAO = 3;
    public static final double MEDIA_MINIMA = 7.0;
    public static final int CURSOS_RELACIONADOS_OFERECIDOS = 10;

    private final List<Curso> catalogoDeCursos;

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
        matricula.concluirCom(media);

        if (aprovadoParaCursosAdicionais(media)) {
            matricula.getAluno().liberarCursosAdicionais(CURSOS_ADICIONAIS_POR_APROVACAO);
        }
    }

    public int cursosAdicionaisLiberadosPara(Aluno aluno) {
        return aluno.getCursosAdicionaisDisponiveis();
    }

    // Cenario do Joao: a regra da media, isolada e publica.
    // A media precisa ser ACIMA de 7,0. Exatamente 7,0 nao libera nada.
    public static boolean aprovadoParaCursosAdicionais(double media) {
        return media > MEDIA_MINIMA;
    }

    // Cenario do Joao: o aluno so fica elegivel enquanto tiver saldo.
    public boolean estaElegivelParaCursosAdicionais(Aluno aluno) {
        return cursosAdicionaisLiberadosPara(aluno) > 0;
    }

    // Cenario do Eduardo: o aluno gasta os cursos adicionais que conquistou.
    // Zerar o saldo aqui e o que permite provar que uma nova aprovacao concede mais 3.
    public void usarCursosAdicionais(Aluno aluno, int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade a usar deve ser maior que zero.");
        }

        if (quantidade > cursosAdicionaisLiberadosPara(aluno)) {
            throw new IllegalStateException("Aluno nao tem cursos adicionais suficientes.");
        }

        for (int i = 0; i < quantidade; i++) {
            aluno.consumirCursoAdicional();
        }
    }

    // Cenario do Felipe: ao resgatar, o aluno recebe 10 cursos da mesma area do
    // que concluiu e pode escolher 3 deles.
    public Resgate iniciarResgate(Matricula matriculaConcluida) {
        Aluno aluno = matriculaConcluida.getAluno();

        if (!estaElegivelParaCursosAdicionais(aluno)) {
            throw new IllegalStateException("Aluno nao tem cursos adicionais para resgatar.");
        }

        Curso cursoConcluido = matriculaConcluida.getCurso();

        List<Curso> relacionados = catalogoDeCursos.stream()
                .filter(curso -> curso.getArea().equals(cursoConcluido.getArea()))
                .filter(curso -> !curso.getTitulo().equals(cursoConcluido.getTitulo()))
                .limit(CURSOS_RELACIONADOS_OFERECIDOS)
                .toList();

        return new Resgate(relacionados, CURSOS_ADICIONAIS_POR_APROVACAO);
    }

    private static List<Curso> catalogoPadrao() {
        return List.of(
                new Curso("Estruturas de Dados", "Curso de programacao", "Programacao"),
                new Curso("Algoritmos Avancados", "Curso de programacao", "Programacao"),
                new Curso("Programacao Orientada a Objetos", "Curso de programacao", "Programacao"),
                new Curso("Programacao Funcional", "Curso de programacao", "Programacao"),
                new Curso("Desenvolvimento Web", "Curso de programacao", "Programacao"),
                new Curso("Desenvolvimento Mobile", "Curso de programacao", "Programacao"),
                new Curso("Banco de Dados", "Curso de programacao", "Programacao"),
                new Curso("Testes Automatizados", "Curso de programacao", "Programacao"),
                new Curso("Arquitetura de Software", "Curso de programacao", "Programacao"),
                new Curso("DevOps Essencial", "Curso de programacao", "Programacao"),
                new Curso("Fundamentos de Design", "Curso de design", "Design"),
                new Curso("Marketing Digital", "Curso de marketing", "Marketing")
        );
    }
}
