package com.example.grupo_1_gameeducator.recomendacoes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.grupo_1_gameeducator.domain.Aluno;
import com.example.grupo_1_gameeducator.domain.Curso;
import com.example.grupo_1_gameeducator.dto.MatriculaResponseDTO;
import com.example.grupo_1_gameeducator.repository.AlunoRepository;
import com.example.grupo_1_gameeducator.repository.CursoRepository;
import com.example.grupo_1_gameeducator.service.MatriculaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

// Desafio 3 (ADR-003): o Core inteiro (Spring + H2) com o servico de IA fora do ar.
// A URL da IA aponta para uma porta sem ninguem escutando.
@SpringBootTest(properties = {
        "ia.base-url=http://127.0.0.1:1",
        "ia.timeout-ms=500"
})
@AutoConfigureMockMvc
@Transactional
class IaForaDoArIntegracaoTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private MatriculaService matriculaService;

    @Test
    @DisplayName("Com a IA fora do ar, GET /recomendacoes responde 200 com origem INDISPONIVEL")
    void recomendacoesRespondemDegradadas() throws Exception {
        Aluno aluno = alunoRepository.save(new Aluno("Ana", "ana.desafio3@teste.com"));

        mockMvc.perform(get("/api/alunos/{id}/recomendacoes", aluno.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.origem").value("INDISPONIVEL"))
                .andExpect(jsonPath("$.personalizadas").value(false))
                .andExpect(jsonPath("$.cursos").isEmpty());
    }

    @Test
    @DisplayName("Com a IA fora do ar, matricular, concluir e resgatar continuam funcionando")
    void fluxoEssencialNaoDependeDaIa() {
        Aluno aluno = alunoRepository.save(new Aluno("Bia", "bia.desafio3@teste.com"));
        Curso curso = cursoRepository.save(new Curso("Logica de Programacao", "Curso base", "Programacao"));
        cursoRepository.save(new Curso("Estruturas de Dados", "Curso de programacao", "Programacao"));

        MatriculaResponseDTO matricula = matriculaService.matricular(aluno.getId(), curso.getId(), false);
        MatriculaResponseDTO concluida = matriculaService.concluir(matricula.getId(), 9.0);

        assertEquals("CONCLUIDO", concluida.getStatus());
        assertEquals(3, concluida.getCursosAdicionaisDisponiveis());
        assertEquals(1, matriculaService.iniciarResgate(matricula.getId()).getCursosDisponiveis().size());
    }
}
