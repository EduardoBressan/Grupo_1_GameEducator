package com.example.grupo_1_gameeducator.domain;

import java.util.HashMap;
import java.util.Map;

// Camada: DOMINIO (TDD).
//
// US (ADM): COMO administrador da plataforma, QUERO que o aluno, ao terminar um curso
// com media acima de 7,0, tenha direito a realizacao de mais 3 cursos, PARA fidelizar
// alunos a longo prazo e garantir qualidade de ensino.
//
// ESTADO ATUAL: PASSO GREEN DO TDD.
// A implementacao mais simples que faz os 5 testes de PlataformaEnsinoTest passarem.
public class PlataformaEnsino {

    public static final int CURSOS_ADICIONAIS_POR_APROVACAO = 3;
    public static final double MEDIA_MINIMA = 7.0;

    // Saldo de cursos adicionais conquistados por cada aluno.
    private final Map<Aluno, Integer> cursosAdicionaisPorAluno = new HashMap<>();

    public Matricula matricular(Aluno aluno, Curso curso) {
        return new Matricula(aluno, curso);
    }

    public void finalizarCurso(Matricula matricula, double media) {
        matricula.registrarResultado(media, SituacaoMatricula.CONCLUIDA);

        // A media precisa ser ACIMA de 7,0. Exatamente 7,0 nao libera nada.
        if (media > MEDIA_MINIMA) {
            Aluno aluno = matricula.getAluno();
            int saldoAtual = cursosAdicionaisPorAluno.getOrDefault(aluno, 0);
            cursosAdicionaisPorAluno.put(aluno, saldoAtual + CURSOS_ADICIONAIS_POR_APROVACAO);
        }
    }

    public int cursosAdicionaisLiberadosPara(Aluno aluno) {
        // Aluno que ainda nao concluiu nada com aproveitamento nao tem saldo.
        return cursosAdicionaisPorAluno.getOrDefault(aluno, 0);
    }
}
