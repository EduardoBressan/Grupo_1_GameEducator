package com.example.grupo_1_gameeducator.conquistas;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.grupo_1_gameeducator.domain.Aluno;
import com.example.grupo_1_gameeducator.domain.Curso;
import com.example.grupo_1_gameeducator.dto.MatriculaResponseDTO;
import com.example.grupo_1_gameeducator.dto.ResgateResponseDTO;
import com.example.grupo_1_gameeducator.repository.AlunoRepository;
import com.example.grupo_1_gameeducator.repository.CursoRepository;
import com.example.grupo_1_gameeducator.service.MatriculaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

// Desafio 1 (ADR-001): prova que o fluxo real da API (MatriculaService, H2)
// passa pelo modulo Conquistas, ou seja, executa a regra coberta pelos testes de dominio.
@SpringBootTest
@Transactional
class ConquistasIntegracaoTest {

    @Autowired
    private MatriculaService matriculaService;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Test
    @DisplayName("Concluir pela API com media 8,5 libera 3 cursos e permite o resgate")
    void concluirPelaApiLiberaCursosEPermiteResgate() {
        Aluno aluno = alunoRepository.save(new Aluno("Ana", "ana.desafio1@teste.com"));
        Curso concluido = cursoRepository.save(new Curso("Logica de Programacao", "Curso base", "Programacao"));
        cursoRepository.save(new Curso("Estruturas de Dados", "Curso de programacao", "Programacao"));
        cursoRepository.save(new Curso("Fundamentos de Design", "Curso de design", "Design"));

        MatriculaResponseDTO matricula = matriculaService.matricular(aluno.getId(), concluido.getId(), false);
        MatriculaResponseDTO concluida = matriculaService.concluir(matricula.getId(), 8.5);
        ResgateResponseDTO resgate = matriculaService.iniciarResgate(matricula.getId());

        assertEquals(3, concluida.getCursosAdicionaisLiberados());
        assertEquals(3, concluida.getCursosAdicionaisDisponiveis());
        assertEquals(1, resgate.getCursosDisponiveis().size());
        assertEquals(3, resgate.getLimiteDeEscolhas());
    }

    @Test
    @DisplayName("Concluir pela API com media 7,0 nao libera cursos")
    void concluirPelaApiComMediaSeteNaoLibera() {
        Aluno aluno = alunoRepository.save(new Aluno("Joao", "joao.desafio1@teste.com"));
        Curso curso = cursoRepository.save(new Curso("Banco de Dados", "Curso base", "Programacao"));

        MatriculaResponseDTO matricula = matriculaService.matricular(aluno.getId(), curso.getId(), false);
        MatriculaResponseDTO concluida = matriculaService.concluir(matricula.getId(), 7.0);

        assertEquals(0, concluida.getCursosAdicionaisLiberados());
        assertEquals(0, concluida.getCursosAdicionaisDisponiveis());
    }
}
