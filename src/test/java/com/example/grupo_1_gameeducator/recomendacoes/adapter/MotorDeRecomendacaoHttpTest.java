package com.example.grupo_1_gameeducator.recomendacoes.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.grupo_1_gameeducator.recomendacoes.CursoRecomendado;
import com.example.grupo_1_gameeducator.recomendacoes.MotorDeRecomendacaoIndisponivelException;
import com.example.grupo_1_gameeducator.recomendacoes.adapter.http.MotorDeRecomendacaoHttp;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

// Desafio 3 (ADR-003): adapter HTTP contra um servidor HTTP local que imita o FastAPI.
// Simula os tres cenarios do mini-lab: IA respondendo, IA fora do ar e IA lenta.
class MotorDeRecomendacaoHttpTest {

    private static final String JSON_DA_IA = """
            {"alunoId": 7, "modelo": "regras-v1", "cursos": [
              {"titulo": "Estruturas de Dados", "area": "Programacao", "motivo": "historico parecido"},
              {"titulo": "Testes Automatizados", "area": "Programacao", "motivo": "historico parecido"}
            ]}
            """;

    private HttpServer servidor;

    @AfterEach
    void pararServidor() {
        if (servidor != null) {
            servidor.stop(0);
        }
    }

    @Test
    @DisplayName("Converte a resposta do FastAPI em cursos recomendados")
    void converteARespostaDaIa() throws IOException {
        String baseUrl = subirServidor(200, JSON_DA_IA, 0);

        List<CursoRecomendado> cursos = new MotorDeRecomendacaoHttp(baseUrl, 1000).recomendarPara(7L);

        assertEquals(2, cursos.size());
        assertEquals("Estruturas de Dados", cursos.get(0).getTitulo());
        assertEquals("Programacao", cursos.get(0).getArea());
    }

    @Test
    @DisplayName("Servico de IA fora do ar vira MotorDeRecomendacaoIndisponivelException")
    void iaForaDoAr() throws IOException {
        String baseUrl = subirServidor(200, JSON_DA_IA, 0);
        servidor.stop(0);
        servidor = null;

        MotorDeRecomendacaoHttp motor = new MotorDeRecomendacaoHttp(baseUrl, 1000);

        assertThrows(MotorDeRecomendacaoIndisponivelException.class, () -> motor.recomendarPara(7L));
    }

    @Test
    @DisplayName("Servico de IA lento e cortado pelo timeout, sem segurar a requisicao")
    void iaLentaECortadaPeloTimeout() throws IOException {
        String baseUrl = subirServidor(200, JSON_DA_IA, 3000);
        MotorDeRecomendacaoHttp motor = new MotorDeRecomendacaoHttp(baseUrl, 300);

        long inicio = System.nanoTime();
        assertThrows(MotorDeRecomendacaoIndisponivelException.class, () -> motor.recomendarPara(7L));
        long duracaoMs = (System.nanoTime() - inicio) / 1_000_000;

        assertTrue(duracaoMs < 2000, "deveria desistir perto de 300 ms, levou " + duracaoMs + " ms");
    }

    @Test
    @DisplayName("Erro 500 do servico de IA tambem vira indisponivel")
    void erroDoServicoDeIa() throws IOException {
        String baseUrl = subirServidor(500, "{\"detail\": \"modelo nao carregado\"}", 0);

        MotorDeRecomendacaoHttp motor = new MotorDeRecomendacaoHttp(baseUrl, 1000);

        assertThrows(MotorDeRecomendacaoIndisponivelException.class, () -> motor.recomendarPara(7L));
    }

    private String subirServidor(int status, String corpo, long atrasoMs) throws IOException {
        servidor = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        servidor.createContext("/recomendacoes/", troca -> {
            try {
                Thread.sleep(atrasoMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            byte[] bytes = corpo.getBytes(StandardCharsets.UTF_8);
            troca.getResponseHeaders().add("Content-Type", "application/json");
            troca.sendResponseHeaders(status, bytes.length);
            try (OutputStream saida = troca.getResponseBody()) {
                saida.write(bytes);
            } catch (IOException e) {
                // o cliente pode ter desistido pelo timeout
            }
        });
        servidor.start();
        return "http://127.0.0.1:" + servidor.getAddress().getPort();
    }
}
