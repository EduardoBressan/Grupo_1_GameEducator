package com.example.grupo_1_gameeducator.recomendacoes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

// Desafio 3 (ADR-003): caso de uso de recomendacao, package-private como no modulo Conquistas.
// Depende so da porta MotorDeRecomendacao; nao sabe que do outro lado ha HTTP nem Python.
// Se o motor falhar, devolve a resposta degradada em vez de propagar o erro.
@Service
class RecomendacoesService implements RecomendacoesApi {

    private static final Logger LOG = LoggerFactory.getLogger(RecomendacoesService.class);

    private final MotorDeRecomendacao motor;

    RecomendacoesService(MotorDeRecomendacao motor) {
        this.motor = motor;
    }

    @Override
    public Recomendacoes recomendarPara(Long alunoId) {
        try {
            List<CursoRecomendado> cursos = motor.recomendarPara(alunoId);
            return Recomendacoes.daIa(alunoId, cursos);
        } catch (MotorDeRecomendacaoIndisponivelException e) {
            LOG.warn("Servico de IA indisponivel; respondendo sem recomendacoes (aluno {}): {}", alunoId, e.getMessage());
            return Recomendacoes.indisponiveis(alunoId);
        }
    }
}
