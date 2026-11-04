package com.example.grupo_1_gameeducator.controller;

import com.example.grupo_1_gameeducator.dto.CursoRecomendadoDTO;
import com.example.grupo_1_gameeducator.dto.RecomendacoesResponseDTO;
import com.example.grupo_1_gameeducator.recomendacoes.Recomendacoes;
import com.example.grupo_1_gameeducator.recomendacoes.RecomendacoesApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Camada: CONTROLLER. Expoe as recomendacoes da IA (Desafio 3, ADR-003).
// Sempre responde 200: quando a IA esta fora, a lista vem vazia com origem INDISPONIVEL.
@RestController
@RequestMapping("/api/alunos")
@Tag(name = "Recomendacoes")
public class RecomendacaoRestController {

    private final RecomendacoesApi recomendacoes;

    public RecomendacaoRestController(RecomendacoesApi recomendacoes) {
        this.recomendacoes = recomendacoes;
    }

    @GetMapping("/{id}/recomendacoes")
    @Operation(summary = "Recomendar cursos para o aluno (servico de IA em Python)")
    public RecomendacoesResponseDTO recomendar(@PathVariable Long id) {
        return toDTO(recomendacoes.recomendarPara(id));
    }

    private RecomendacoesResponseDTO toDTO(Recomendacoes resultado) {
        List<CursoRecomendadoDTO> cursos = resultado.getCursos().stream()
                .map(curso -> new CursoRecomendadoDTO(curso.getTitulo(), curso.getArea(), curso.getMotivo()))
                .toList();

        String mensagem = resultado.isPersonalizadas()
                ? "Recomendacoes personalizadas pelo servico de IA."
                : "Recomendacoes indisponiveis no momento. Matricula, conclusao e resgate continuam funcionando.";

        return new RecomendacoesResponseDTO(
                resultado.getAlunoId(),
                resultado.getOrigem().name(),
                resultado.isPersonalizadas(),
                mensagem,
                cursos
        );
    }
}
