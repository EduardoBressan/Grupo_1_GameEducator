package com.example.grupo_1_gameeducator.dto;

import java.util.List;

// Camada: DTO. Saida da API de recomendacoes (Desafio 3).
// "origem" diz se a lista veio da IA ou se o servico estava indisponivel.
public class RecomendacoesResponseDTO {

    private Long alunoId;
    private String origem;
    private boolean personalizadas;
    private String mensagem;
    private List<CursoRecomendadoDTO> cursos;

    public RecomendacoesResponseDTO() {
    }

    public RecomendacoesResponseDTO(Long alunoId, String origem, boolean personalizadas,
                                    String mensagem, List<CursoRecomendadoDTO> cursos) {
        this.alunoId = alunoId;
        this.origem = origem;
        this.personalizadas = personalizadas;
        this.mensagem = mensagem;
        this.cursos = cursos;
    }

    public Long getAlunoId() {
        return alunoId;
    }

    public String getOrigem() {
        return origem;
    }

    public boolean isPersonalizadas() {
        return personalizadas;
    }

    public String getMensagem() {
        return mensagem;
    }

    public List<CursoRecomendadoDTO> getCursos() {
        return cursos;
    }
}
