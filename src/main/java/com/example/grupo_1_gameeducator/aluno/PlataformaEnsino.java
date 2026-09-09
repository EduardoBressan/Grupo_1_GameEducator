package com.example.grupo_1_gameeducator.aluno;

public class PlataformaEnsino {

    // -1 pra garantir que os testes que esperam 0 tambem falhem aqui no RED
    private static final int SEM_REGRA = -1;

    public static final int CURSOS_EXTRA = 3;
    public static final double MEDIA_MINIMA = 7.0;

    public Matricula matricular(Aluno aluno, Curso curso) {
        return new Matricula(aluno, curso);
    }

    public void finalizarCurso(Matricula matricula, double media) {
        // TODO implementar: concluir matricula e liberar cursos se media > 7,0
    }

    public int cursosAdicionaisLiberadosPara(Aluno aluno) {
        return SEM_REGRA;
    }
}
