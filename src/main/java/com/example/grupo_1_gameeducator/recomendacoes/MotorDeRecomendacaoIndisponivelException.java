package com.example.grupo_1_gameeducator.recomendacoes;

// Desafio 3 (ADR-003): o motor de recomendacao nao respondeu (fora do ar, lento ou com erro).
public class MotorDeRecomendacaoIndisponivelException extends RuntimeException {

    public MotorDeRecomendacaoIndisponivelException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }

    public MotorDeRecomendacaoIndisponivelException(String mensagem) {
        super(mensagem);
    }
}
