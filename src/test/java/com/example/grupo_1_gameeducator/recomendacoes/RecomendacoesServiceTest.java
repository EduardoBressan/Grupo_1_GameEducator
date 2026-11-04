package com.example.grupo_1_gameeducator.recomendacoes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

// Desafio 3 (ADR-003): o caso de uso trata a falha da IA sem propagar erro.
// O motor e substituido por lambdas (sem Spring, sem HTTP, sem Python).
class RecomendacoesServiceTest {

    @Test
    @DisplayName("Com a IA respondendo, devolve as recomendacoes personalizadas")
    void comIaRespondendoDevolveRecomendacoes() {
        MotorDeRecomendacao motor = alunoId -> List.of(
                new CursoRecomendado("Estruturas de Dados", "Programacao", "historico parecido"),
                new CursoRecomendado("Testes Automatizados", "Programacao", "historico parecido"));

        Recomendacoes resultado = new RecomendacoesService(motor).recomendarPara(1L);

        assertEquals(Recomendacoes.Origem.IA, resultado.getOrigem());
        assertTrue(resultado.isPersonalizadas());
        assertEquals(2, resultado.getCursos().size());
    }

    @Test
    @DisplayName("Com a IA fora do ar, devolve resposta degradada sem lancar excecao")
    void comIaForaDoArDevolveRespostaDegradada() {
        MotorDeRecomendacao motor = alunoId -> {
            throw new MotorDeRecomendacaoIndisponivelException("Connection refused");
        };

        Recomendacoes resultado = new RecomendacoesService(motor).recomendarPara(1L);

        assertEquals(Recomendacoes.Origem.INDISPONIVEL, resultado.getOrigem());
        assertFalse(resultado.isPersonalizadas());
        assertTrue(resultado.getCursos().isEmpty());
        assertEquals(1L, resultado.getAlunoId().longValue());
    }
}
