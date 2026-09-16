package com.example.grupo_1_gameeducator.domainTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.grupo_1_gameeducator.domain.Aluno;
import java.lang.reflect.Constructor;
import org.junit.jupiter.api.Test;

class AlunoTest {

    @Test
    void deveComecarSemSaldo() {
        Aluno aluno = new Aluno("Luiza");

        assertNull(aluno.getId());
        assertNull(aluno.getEmail());
        assertEquals("Luiza", aluno.getNome());
        assertEquals(0, aluno.getCursosAdicionaisDisponiveis());
    }

    @Test
    void deveGuardarEmail() {
        Aluno aluno = new Aluno("Luiza", "LUIZA@exemplo.com");

        assertEquals("luiza@exemplo.com", aluno.getEmail());
    }

    @Test
    void deveSomarAoSaldo() {
        Aluno aluno = new Aluno("Luiza");

        aluno.liberarCursosAdicionais(3);
        aluno.liberarCursosAdicionais(3);

        assertEquals(6, aluno.getCursosAdicionaisDisponiveis());
    }

    @Test
    void deveRecusarQuantidadeNegativa() {
        Aluno aluno = new Aluno("Luiza");

        assertThrows(IllegalArgumentException.class, () -> aluno.liberarCursosAdicionais(-1));
    }

    @Test
    void deveDiminuirOSaldo() {
        Aluno aluno = new Aluno("Luiza");
        aluno.liberarCursosAdicionais(3);

        aluno.consumirCursoAdicional();

        assertEquals(2, aluno.getCursosAdicionaisDisponiveis());
    }

    @Test
    void deveRecusarConsumoSemSaldo() {
        Aluno aluno = new Aluno("Luiza");

        assertThrows(IllegalStateException.class, aluno::consumirCursoAdicional);
    }

    @Test
    void deveAlterarNomeEEmail() {
        Aluno aluno = new Aluno("Luiza", "luiza@exemplo.com");

        aluno.alterarNome("Luiza Bottesi");
        aluno.alterarEmail("bottesi@exemplo.com");

        assertEquals("Luiza Bottesi", aluno.getNome());
        assertEquals("bottesi@exemplo.com", aluno.getEmail());
    }

    // Construtor do JPA, chamado por reflexao porque e protegido.
    @Test
    void construtorDoJpaDeveExistir() throws Exception {
        Constructor<Aluno> construtor = Aluno.class.getDeclaredConstructor();
        construtor.setAccessible(true);

        assertNotNull(construtor.newInstance());
    }
}
