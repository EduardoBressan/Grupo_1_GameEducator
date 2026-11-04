package com.example.grupo_1_gameeducator.recomendacoes;

import java.util.List;

// Desafio 3 (ADR-003): porta de saida para o motor de recomendacao.
// Hoje quem implementa e o adapter HTTP que chama o servico Python (FastAPI).
// Se o motor nao responder, o adapter lanca MotorDeRecomendacaoIndisponivelException.
public interface MotorDeRecomendacao {

    List<CursoRecomendado> recomendarPara(Long alunoId);
}
