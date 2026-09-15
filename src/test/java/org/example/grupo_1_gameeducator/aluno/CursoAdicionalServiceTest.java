package org.example.grupo_1_gameeducator.aluno;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CursoAdicionalServiceTest {

    private final CursoAdicionalService service = new CursoAdicionalService();

    @Test
    @DisplayName("Dado que já usou os 3 cursos anteriores, quando finalizar novo curso com média > 7,0, então concede mais 3")
    void deveConcederNovamente3CursosAposUsoDosAnteriores() {
        // Arrange
        Aluno aluno = new Aluno("Bruno", 7.5);
        aluno.getCursosAdicionaisConcedidos().addAll(List.of(
                new Curso("Curso A", "Relacionado"),
                new Curso("Curso B", "Relacionado"),
                new Curso("Curso C", "Relacionado")
        ));
        List<Curso> novosRelacionados = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            novosRelacionados.add(new Curso("Novo Curso " + i, "Relacionado"));
        }

        // Action
        List<Curso> novosConcedidos = service.concederCursosAdicionais(aluno, novosRelacionados);

        // Assert
        assertThat(novosConcedidos).hasSize(3);
    }
}