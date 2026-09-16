package com.example.grupo_1_gameeducator.domainTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.grupo_1_gameeducator.domain.Aluno;
import com.example.grupo_1_gameeducator.domain.Curso;
import com.example.grupo_1_gameeducator.domain.PlataformaEnsino;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// Integrante: Joao
//
// BDD: DADO QUE o aluno finalize o curso com media exatamente 7,0,
// QUANDO o sistema avaliar a elegibilidade dele,
// ENTAO ele nao deve ficar elegivel aos 3 cursos adicionais.
//
// Todos os testes seguem o padrao AAA (Arrange, Act, Assert).
class ElegibilidadeDeCursosAdicionaisTest {

    private PlataformaEnsino plataforma;
    private Aluno aluno;
    private Curso curso;

    @BeforeEach
    void setUp() {
        plataforma = new PlataformaEnsino();
        aluno = new Aluno("Joao");
        curso = new Curso("Logica de Programacao", "Programacao");
    }

    @Test
    @DisplayName("Dado que finalizou com media exatamente 7,0, quando avaliar a elegibilidade, entao o aluno nao fica elegivel")
    void naoDeveFicarElegivelComMediaExatamenteSete() {
        // Arrange
        var matricula = plataforma.matricular(aluno, curso);

        // Act
        plataforma.finalizarCurso(matricula, 7.0);

        // Assert
        assertFalse(plataforma.estaElegivelParaCursosAdicionais(aluno));
        assertEquals(0, plataforma.cursosAdicionaisLiberadosPara(aluno));
    }

    @Test
    @DisplayName("Dado que finalizou com media acima de 7,0, quando avaliar a elegibilidade, entao o aluno fica elegivel")
    void deveFicarElegivelComMediaAcimaDeSete() {
        // Arrange
        var matricula = plataforma.matricular(aluno, curso);

        // Act
        plataforma.finalizarCurso(matricula, 7.1);

        // Assert
        assertTrue(plataforma.estaElegivelParaCursosAdicionais(aluno));
        assertEquals(3, plataforma.cursosAdicionaisLiberadosPara(aluno));
    }

    @Test
    @DisplayName("A regra da plataforma exige media ACIMA de 7,0: 6,99 e 7,0 nao aprovam, 7,01 aprova")
    void regraDeMediaDeveExigirNotaAcimaDeSete() {
        // Arrange + Act + Assert (regra pura, sem precisar de matricula)
        assertFalse(PlataformaEnsino.aprovadoParaCursosAdicionais(6.99));
        assertFalse(PlataformaEnsino.aprovadoParaCursosAdicionais(7.0));
        assertTrue(PlataformaEnsino.aprovadoParaCursosAdicionais(7.01));
    }

    @Test
    @DisplayName("Dado que o aluno usou todos os cursos adicionais, quando avaliar a elegibilidade, entao ele deixa de estar elegivel")
    void deveDeixarDeSerElegivelAposUsarTodosOsCursosAdicionais() {
        // Arrange
        plataforma.finalizarCurso(plataforma.matricular(aluno, curso), 8.0);

        // Act
        plataforma.usarCursosAdicionais(aluno, 3);

        // Assert
        assertFalse(plataforma.estaElegivelParaCursosAdicionais(aluno));
    }

    @Test
    @DisplayName("Dado que o aluno nunca concluiu nada, quando avaliar a elegibilidade, entao ele nao esta elegivel")
    void naoDeveEstarElegivelSemNenhumCursoConcluido() {
        // Arrange + Act + Assert
        assertFalse(plataforma.estaElegivelParaCursosAdicionais(aluno));
    }
}
