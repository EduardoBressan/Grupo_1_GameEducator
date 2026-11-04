package com.example.grupo_1_gameeducator.conquistas.adapter;

import com.example.grupo_1_gameeducator.conquistas.CatalogoDeCursos;
import com.example.grupo_1_gameeducator.conquistas.adapter.jpa.CatalogoDeCursosJpa;
import com.example.grupo_1_gameeducator.domain.Curso;
import com.example.grupo_1_gameeducator.repository.CursoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

// Desafio 2 (ADR-002): o mesmo contrato da porta rodando contra o adapter JPA (Spring Data + H2).
@DataJpaTest
@Import(CatalogoDeCursosJpa.class)
class CatalogoDeCursosJpaTest extends CatalogoDeCursosContrato {

    @Autowired
    private CatalogoDeCursosJpa catalogo;

    @Autowired
    private CursoRepository cursoRepository;

    // Os metodos de teste sao herdados do contrato (outra classe), e o rollback automatico do
    // @DataJpaTest so vale para metodos declarados na classe anotada. Por isso, cada teste
    // comeca limpando a tabela, para um cenario nao enxergar os cursos gravados pelo anterior.
    @BeforeEach
    void limparCursos() {
        cursoRepository.deleteAll();
    }

    @Override
    protected CatalogoDeCursos catalogo() {
        return catalogo;
    }

    @Override
    protected Curso cadastrar(Curso curso) {
        return cursoRepository.save(curso);
    }
}
