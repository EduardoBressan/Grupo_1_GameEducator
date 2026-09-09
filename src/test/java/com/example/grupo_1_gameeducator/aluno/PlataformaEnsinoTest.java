package com.example.grupo_1_gameeducator.aluno;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// BDD: dado que o aluno esta cursando um curso na plataforma, quando ele
// finalizar o curso com media acima de 7,0, entao ele deve ter direito a
// realizar mais 3 cursos.
class PlataformaEnsinoTest {

    private PlataformaEnsino plataforma;
    private Aluno aluno;
    private Curso curso;

    @BeforeEach
    void setUp() {
        plataforma = new PlataformaEnsino();
        aluno = new Aluno("Luiza");
        curso = new Curso("Logica de Programacao");
    }

    @Test
    void deveLiberarTresCursosQuandoMediaForAcimaDeSete() {
        Matricula matricula = plataforma.matricular(aluno, curso);

        plataforma.finalizarCurso(matricula, 8.5);

        assertEquals(3, plataforma.cursosAdicionaisLiberadosPara(aluno));
    }

    @Test
    void matriculaDeveFicarConcluidaAposFinalizarCurso() {
        Matricula matricula = plataforma.matricular(aluno, curso);

        plataforma.finalizarCurso(matricula, 9.0);

        assertEquals(SituacaoMatricula.CONCLUIDA, matricula.getSituacao());
        assertEquals(9.0, matricula.getMedia(), 0.0001);
    }

    @Test
    void naoDeveLiberarCursoComMediaExatamenteSete() {
        Matricula matricula = plataforma.matricular(aluno, curso);

        plataforma.finalizarCurso(matricula, 7.0);

        assertEquals(0, plataforma.cursosAdicionaisLiberadosPara(aluno));
    }

    @Test
    void naoDeveLiberarCursoComMediaAbaixoDeSete() {
        Matricula matricula = plataforma.matricular(aluno, curso);

        plataforma.finalizarCurso(matricula, 5.0);

        assertEquals(0, plataforma.cursosAdicionaisLiberadosPara(aluno));
    }

    @Test
    void naoDeveLiberarCursoEnquantoAindaEstiverCursando() {
        plataforma.matricular(aluno, curso);

        assertEquals(0, plataforma.cursosAdicionaisLiberadosPara(aluno));
    }
}
