package com.example.grupo_1_gameeducator.domainTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.grupo_1_gameeducator.domain.Aluno;
import com.example.grupo_1_gameeducator.domain.Curso;
import com.example.grupo_1_gameeducator.domain.Matricula;
import com.example.grupo_1_gameeducator.domain.PlataformaEnsino;
import com.example.grupo_1_gameeducator.domain.Resgate;
import java.util.List;
import org.junit.jupiter.api.Test;

// Casos de borda das regras, fora dos quatro cenarios BDD.
class PlataformaEnsinoRegrasTest {

    @Test
    void deveRecusarQuantidadeInvalida() {
        PlataformaEnsino plataforma = new PlataformaEnsino();
        Aluno aluno = new Aluno("Luiza");

        assertThrows(IllegalArgumentException.class, () -> plataforma.usarCursosAdicionais(aluno, 0));
    }

    @Test
    void deveRecusarUsoAcimaDoSaldo() {
        PlataformaEnsino plataforma = new PlataformaEnsino();
        Aluno aluno = new Aluno("Luiza");
        plataforma.finalizarCurso(
                plataforma.matricular(aluno, new Curso("Logica", "Curso base", "Programacao")), 8.0);

        assertThrows(IllegalStateException.class, () -> plataforma.usarCursosAdicionais(aluno, 5));
    }

    @Test
    void naoDeveOferecerOCursoJaConcluido() {
        // "Banco de Dados" esta no catalogo padrao, na area Programacao.
        PlataformaEnsino plataforma = new PlataformaEnsino();
        Aluno aluno = new Aluno("Luiza");
        Curso concluido = new Curso("Banco de Dados", "Curso de programacao", "Programacao");
        Matricula matricula = plataforma.matricular(aluno, concluido);
        plataforma.finalizarCurso(matricula, 8.0);

        Resgate resgate = plataforma.iniciarResgate(matricula);

        assertEquals(9, resgate.getCursosDisponiveis().size());
        assertTrue(resgate.getCursosDisponiveis().stream()
                .noneMatch(curso -> curso.getTitulo().equals("Banco de Dados")));
    }

    @Test
    void deveAceitarCatalogoProprio() {
        PlataformaEnsino plataforma = new PlataformaEnsino(List.of(
                new Curso("Ingles Tecnico", "Curso de idiomas", "Idiomas"),
                new Curso("Espanhol Basico", "Curso de idiomas", "Idiomas")));
        Aluno aluno = new Aluno("Luiza");
        Curso concluido = new Curso("Ingles Tecnico", "Curso de idiomas", "Idiomas");
        Matricula matricula = plataforma.matricular(aluno, concluido);
        plataforma.finalizarCurso(matricula, 9.0);

        Resgate resgate = plataforma.iniciarResgate(matricula);

        assertEquals(1, resgate.getCursosDisponiveis().size());
        assertEquals("Espanhol Basico", resgate.getCursosDisponiveis().get(0).getTitulo());
    }
}
