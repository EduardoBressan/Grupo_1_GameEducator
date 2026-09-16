package com.example.grupo_1_gameeducator.domainTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.grupo_1_gameeducator.domain.Aluno;
import com.example.grupo_1_gameeducator.domain.Curso;
import com.example.grupo_1_gameeducator.domain.PlataformaEnsino;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// BDD do Eduardo Bressan Paixao:
// DADO QUE o aluno ja utilizou os 3 cursos adicionais concedidos anteriormente,
// QUANDO ele finalizar um novo curso com media acima de 7,0,
// ENTAO o sistema deve conceder novamente mais 3 cursos adicionais.
class CursosAdicionaisAposUsoTest {

    @Test
    @DisplayName("Dado que ja usou os 3 cursos anteriores, quando finalizar novo curso com media acima de 7,0, entao concede mais 3")
    void deveConcederNovamenteTresCursosAposUsoDosAnteriores() {
        // Arrange
        PlataformaEnsino plataforma = new PlataformaEnsino();
        Aluno bruno = new Aluno("Bruno");

        plataforma.finalizarCurso(
                plataforma.matricular(bruno,
                        new Curso("Logica de Programacao", "Curso base", "Programacao")), 7.5);
        plataforma.usarCursosAdicionais(bruno, 3);

        // Act
        plataforma.finalizarCurso(
                plataforma.matricular(bruno,
                        new Curso("Banco de Dados", "Curso de programacao", "Programacao")), 8.0);

        // Assert
        assertEquals(3, plataforma.cursosAdicionaisLiberadosPara(bruno));
    }
}
