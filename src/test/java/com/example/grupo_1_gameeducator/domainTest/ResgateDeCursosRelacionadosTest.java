package com.example.grupo_1_gameeducator.domainTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.grupo_1_gameeducator.domain.Aluno;
import com.example.grupo_1_gameeducator.domain.Curso;
import com.example.grupo_1_gameeducator.domain.Matricula;
import com.example.grupo_1_gameeducator.domain.PlataformaEnsino;
import com.example.grupo_1_gameeducator.domain.Resgate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// Integrante: Felipe Rondello A. Lopes
//
// BDD: DADO QUE o aluno finalize o curso com media maior que 7,0,
// QUANDO for resgatar seus 3 cursos,
// ENTAO tenha a escolha de resgatar 3 entre 10 cursos relacionados ao que concluiu.
//
// O segundo teste cobre tambem o BDD do Joao: media exatamente 7,0 nao da direito
// a cursos adicionais, e portanto nao permite resgate.
class ResgateDeCursosRelacionadosTest {

    @Test
    @DisplayName("Dado que finalizou com media maior que 7,0, quando for resgatar, entao escolhe 3 entre 10 cursos relacionados")
    void deveOferecerDezCursosRelacionadosParaEscolherTres() {
        // Arrange
        PlataformaEnsino plataforma = new PlataformaEnsino();
        Aluno joao = new Aluno("Joao");
        Curso concluido = new Curso("Logica de Programacao", "Curso base", "Programacao");
        Matricula matricula = plataforma.matricular(joao, concluido);
        plataforma.finalizarCurso(matricula, 8.5);

        // Act
        Resgate resgate = plataforma.iniciarResgate(matricula);

        // Assert
        assertEquals(10, resgate.getCursosDisponiveis().size());
        assertEquals(3, resgate.getLimiteDeEscolhas());
        assertTrue(resgate.getCursosDisponiveis().stream()
                .allMatch(curso -> curso.getArea().equals("Programacao")));
    }

    @Test
    @DisplayName("Dado que finalizou com media igual a 7,0, quando tentar resgatar, entao nao tem direito ao resgate")
    void naoDeveOferecerResgateQuandoMediaNaoUltrapassaSete() {
        // Arrange
        PlataformaEnsino plataforma = new PlataformaEnsino();
        Aluno maria = new Aluno("Maria");
        Curso concluido = new Curso("Logica de Programacao", "Curso base", "Programacao");
        Matricula matricula = plataforma.matricular(maria, concluido);
        plataforma.finalizarCurso(matricula, 7.0);

        // Act + Assert
        assertThrows(IllegalStateException.class, () -> plataforma.iniciarResgate(matricula));
    }
}
