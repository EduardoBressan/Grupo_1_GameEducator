package com.example.grupo_1_gameeducator.domainTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.grupo_1_gameeducator.domain.Aluno;
import com.example.grupo_1_gameeducator.domain.Curso;
import com.example.grupo_1_gameeducator.domain.PlataformaEnsino;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// Integrante: Eduardo Bressan Paixao
//
// BDD: DADO QUE o aluno ja utilizou os 3 cursos adicionais concedidos anteriormente,
// QUANDO ele finalizar um novo curso com media acima de 7,0,
// ENTAO o sistema deve conceder novamente mais 3 cursos adicionais.
class CursosAdicionaisAposUsoTest {

    @Test
    @DisplayName("Dado que ja usou os 3 cursos anteriores, quando finalizar novo curso com media acima de 7,0, entao concede mais 3")
    void deveConcederNovamenteTresCursosAposUsoDosAnteriores() {
        // Arrange: o aluno concluiu um curso, ganhou 3 adicionais e gastou os 3.
        PlataformaEnsino plataforma = new PlataformaEnsino();
        Aluno bruno = new Aluno("Bruno");

        plataforma.finalizarCurso(
                plataforma.matricular(bruno, new Curso("Logica de Programacao", "Programacao")), 7.5);
        plataforma.usarCursosAdicionais(bruno, 3);

        // Act: conclui um novo curso, de novo com media acima de 7,0.
        plataforma.finalizarCurso(
                plataforma.matricular(bruno, new Curso("Banco de Dados", "Programacao")), 8.0);

        // Assert
        assertEquals(3, plataforma.cursosAdicionaisLiberadosPara(bruno));
    }
}
