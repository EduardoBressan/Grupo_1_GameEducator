package com.example.grupo_1_gameeducator.domainTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.grupo_1_gameeducator.domain.Aluno;
import com.example.grupo_1_gameeducator.domain.Curso;
import com.example.grupo_1_gameeducator.domain.Matricula;
import com.example.grupo_1_gameeducator.domain.StatusMatricula;
import java.lang.reflect.Constructor;
import org.junit.jupiter.api.Test;

class MatriculaTest {

    private final Aluno aluno = new Aluno("Luiza");
    private final Curso curso = new Curso("Logica de Programacao");

    @Test
    void deveNascerEmAndamento() {
        Matricula matricula = new Matricula(aluno, curso);

        assertNull(matricula.getId());
        assertEquals(aluno, matricula.getAluno());
        assertEquals(curso, matricula.getCurso());
        assertEquals(StatusMatricula.EM_ANDAMENTO, matricula.getStatus());
        assertNull(matricula.getMediaFinal());
        assertFalse(matricula.isCursoAdicional());
    }

    @Test
    void deveMarcarCursoAdicional() {
        Matricula matricula = new Matricula(aluno, curso, true);

        assertTrue(matricula.isCursoAdicional());
    }

    @Test
    void deveConcluirComMedia() {
        Matricula matricula = new Matricula(aluno, curso);

        matricula.concluirCom(8.0);

        assertEquals(StatusMatricula.CONCLUIDO, matricula.getStatus());
        assertEquals(8.0, matricula.getMediaFinal(), 0.0001);
    }

    @Test
    void statusDeveTerTresValores() {
        assertEquals(3, StatusMatricula.values().length);
        assertEquals(StatusMatricula.CONCLUIDO, StatusMatricula.valueOf("CONCLUIDO"));
    }

    // Construtor do JPA, chamado por reflexao porque e protegido.
    @Test
    void construtorDoJpaDeveExistir() throws Exception {
        Constructor<Matricula> construtor = Matricula.class.getDeclaredConstructor();
        construtor.setAccessible(true);

        assertNotNull(construtor.newInstance());
    }
}
