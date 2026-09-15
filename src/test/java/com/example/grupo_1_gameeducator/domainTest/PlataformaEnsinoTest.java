package com.example.grupo_1_gameeducator.domainTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.grupo_1_gameeducator.domain.Aluno;
import com.example.grupo_1_gameeducator.domain.Curso;
import com.example.grupo_1_gameeducator.domain.Matricula;
import com.example.grupo_1_gameeducator.domain.PlataformaEnsino;
import com.example.grupo_1_gameeducator.domain.SituacaoMatricula;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// BDD (Luiza Bottesi):
// DADO QUE o aluno esta cursando um curso na plataforma,
// QUANDO ele finalizar o curso com media acima de 7,0,
// ENTAO ele deve ter direito a realizar mais 3 cursos.
//
// Todos os testes seguem o padrao AAA (Arrange, Act, Assert).
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
