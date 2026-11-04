package com.example.grupo_1_gameeducator.recomendacoes.adapter.http;

import com.example.grupo_1_gameeducator.recomendacoes.CursoRecomendado;
import com.example.grupo_1_gameeducator.recomendacoes.MotorDeRecomendacao;
import com.example.grupo_1_gameeducator.recomendacoes.MotorDeRecomendacaoIndisponivelException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.util.List;

// Desafio 3 (ADR-003): adapter de saida que chama o servico Python (FastAPI) por HTTP/JSON.
// O timeout e curto de proposito: um modelo lento nao pode segurar a requisicao do Core.
@Component
public class MotorDeRecomendacaoHttp implements MotorDeRecomendacao {

    private final RestClient http;

    public MotorDeRecomendacaoHttp(@Value("${ia.base-url:http://localhost:8000}") String baseUrl,
                                   @Value("${ia.timeout-ms:2000}") long timeoutMs) {
        SimpleClientHttpRequestFactory fabrica = new SimpleClientHttpRequestFactory();
        fabrica.setConnectTimeout(Duration.ofMillis(timeoutMs));
        fabrica.setReadTimeout(Duration.ofMillis(timeoutMs));

        this.http = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(fabrica)
                .build();
    }

    @Override
    public List<CursoRecomendado> recomendarPara(Long alunoId) {
        RespostaIa resposta;
        try {
            resposta = http.get()
                    .uri("/recomendacoes/{alunoId}", alunoId)
                    .retrieve()
                    .body(RespostaIa.class);
        } catch (RestClientException e) {
            throw new MotorDeRecomendacaoIndisponivelException("Servico de IA nao respondeu: " + e.getMessage(), e);
        }

        if (resposta == null || resposta.cursos == null) {
            throw new MotorDeRecomendacaoIndisponivelException("Servico de IA respondeu sem cursos.");
        }

        return resposta.cursos.stream()
                .map(curso -> new CursoRecomendado(curso.titulo, curso.area, curso.motivo))
                .toList();
    }

    // Formato do JSON devolvido pelo FastAPI (GET /recomendacoes/{alunoId}).
    public static class RespostaIa {
        public Long alunoId;
        public String modelo;
        public List<CursoIa> cursos;
    }

    public static class CursoIa {
        public String titulo;
        public String area;
        public String motivo;
    }
}
