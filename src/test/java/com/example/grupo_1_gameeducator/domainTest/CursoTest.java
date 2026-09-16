package com.example.grupo_1_gameeducator.domainTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.example.grupo_1_gameeducator.domain.Curso;
import java.lang.reflect.Constructor;
import org.junit.jupiter.api.Test;

class CursoTest {

    @Test
    void deveUsarPadroes() {
        Curso curso = new Curso("Logica de Programacao");

        assertNull(curso.getId());
        assertEquals("Logica de Programacao", curso.getTitulo());
        assertNull(curso.getDescricao());
        assertEquals("Geral", curso.getArea());
    }

    @Test
    void deveGuardarDescricao() {
        Curso curso = new Curso("Logica de Programacao", "Curso introdutorio");

        assertEquals("Curso introdutorio", curso.getDescricao());
        assertEquals("Geral", curso.getArea());
    }

    @Test
    void deveGuardarArea() {
        Curso curso = new Curso("Banco de Dados", "Curso de programacao", "Programacao");

        assertEquals("Banco de Dados", curso.getTitulo());
        assertEquals("Curso de programacao", curso.getDescricao());
        assertEquals("Programacao", curso.getArea());
    }

    @Test
    void deveAlterarTituloEDescricao() {
        Curso curso = new Curso("Logica", "Antiga");

        curso.alterarTitulo("Logica de Programacao");
        curso.alterarDescricao("Nova descricao");

        assertEquals("Logica de Programacao", curso.getTitulo());
        assertEquals("Nova descricao", curso.getDescricao());
    }

    // Construtor do JPA, chamado por reflexao porque e protegido.
    // Curso sem descricao no banco chega com o VO nulo.
    @Test
    void cursoDoJpaSemDescricaoDeveDevolverNulo() throws Exception {
        Constructor<Curso> construtor = Curso.class.getDeclaredConstructor();
        construtor.setAccessible(true);
        Curso curso = construtor.newInstance();

        assertNull(curso.getDescricao());
    }
}
