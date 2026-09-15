package com.example.grupo_1_gameeducator.domain;

// Camada: DOMINIO (TDD).
//
// US (ADM): COMO administrador da plataforma, QUERO que o aluno, ao terminar um curso
// com media acima de 7,0, tenha direito a realizacao de mais 3 cursos, PARA fidelizar
// alunos a longo prazo e garantir qualidade de ensino.
//
// ESTADO ATUAL: PASSO RED DO TDD.
// Os metodos abaixo sao stubs de proposito: o objetivo desta etapa e que TODOS os
// testes de PlataformaEnsinoTest falhem antes de existir implementacao.
public class PlataformaEnsino {

    // -1 (e nao 0) para garantir que ate os testes negativos, que esperam 0, falhem no RED.
    private static final int NAO_IMPLEMENTADO = -1;

    public static final int CURSOS_ADICIONAIS_POR_APROVACAO = 3;
    public static final double MEDIA_MINIMA = 7.0;

    public Matricula matricular(Aluno aluno, Curso curso) {
        return new Matricula(aluno, curso);
    }

    public void finalizarCurso(Matricula matricula, double media) {
        // TODO GREEN: concluir a matricula e liberar 3 cursos quando media > 7,0.
    }

    public int cursosAdicionaisLiberadosPara(Aluno aluno) {
        // TODO GREEN: devolver o saldo de cursos adicionais do aluno.
        return NAO_IMPLEMENTADO;
    }
}
