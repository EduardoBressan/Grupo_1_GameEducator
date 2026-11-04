package com.example.grupo_1_gameeducator.recomendacoes;

// Desafio 3 (ADR-003): porta de entrada do modulo Recomendacoes.
// Quem chama nunca recebe excecao por causa da IA: se o servico Python estiver
// fora do ar ou lento, a resposta volta marcada como INDISPONIVEL (comportamento degradado).
public interface RecomendacoesApi {

    Recomendacoes recomendarPara(Long alunoId);
}
