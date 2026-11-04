package com.example.grupo_1_gameeducator.conquistas.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.grupo_1_gameeducator.conquistas.CatalogoDeCursos;
import com.example.grupo_1_gameeducator.domain.Curso;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

// Desafio 2 (ADR-002): contrato da porta CatalogoDeCursos.
// Os mesmos testes rodam contra os dois adapters (em memoria e JPA),
// o que mostra que um pode substituir o outro sem mudar o caso de uso.
public abstract class CatalogoDeCursosContrato {

    protected abstract CatalogoDeCursos catalogo();

    protected abstract Curso cadastrar(Curso curso);

    @Test
    @DisplayName("Devolve so os cursos da mesma area do curso concluido")
    void devolveSoCursosDaMesmaArea() {
        Curso concluido = cadastrar(new Curso("Logica de Programacao", "Curso base", "Programacao"));
        cadastrar(new Curso("Estruturas de Dados", "Curso de programacao", "Programacao"));
        cadastrar(new Curso("Fundamentos de Design", "Curso de design", "Design"));

        List<String> titulos = catalogo().relacionadosA(concluido).stream()
                .map(Curso::getTitulo)
                .toList();

        assertEquals(List.of("Estruturas de Dados"), titulos);
    }

    @Test
    @DisplayName("Nao devolve o proprio curso concluido")
    void naoDevolveOProprioCurso() {
        Curso concluido = cadastrar(new Curso("Banco de Dados", "Curso base", "Programacao"));

        assertTrue(catalogo().relacionadosA(concluido).isEmpty());
    }

    @Test
    @DisplayName("Devolve todos os cursos da area; o limite de 10 e regra do dominio, nao do adapter")
    void devolveTodosOsCursosDaArea() {
        Curso concluido = cadastrar(new Curso("Logica de Programacao", "Curso base", "Programacao"));
        for (int i = 1; i <= 12; i++) {
            cadastrar(new Curso("Curso " + i, "Curso de programacao", "Programacao"));
        }

        assertEquals(12, catalogo().relacionadosA(concluido).size());
    }
}
