package com.example.grupo_1_gameeducator.recomendacoes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

// Desafio 3 (ADR-003): teste de arquitetura da fronteira da IA.
// Garante que o fluxo essencial nao chama a IA e que so o adapter conhece HTTP.
class FronteiraIaTest {

    private static final Path RAIZ = Path.of("src/main/java/com/example/grupo_1_gameeducator");

    @Test
    @DisplayName("Dominio, Conquistas e Matricula nao dependem do modulo de recomendacoes")
    void fluxoEssencialNaoDependeDaIa() throws IOException {
        List<Path> fluxoEssencial = new ArrayList<>();
        fluxoEssencial.addAll(arquivosJava(RAIZ.resolve("domain"), true));
        fluxoEssencial.addAll(arquivosJava(RAIZ.resolve("conquistas"), true));
        fluxoEssencial.add(RAIZ.resolve("service/MatriculaService.java"));

        assertEquals(List.of(), violacoes(fluxoEssencial, List.of("grupo_1_gameeducator.recomendacoes")));
    }

    @Test
    @DisplayName("O nucleo do modulo de recomendacoes nao conhece HTTP; so o adapter conhece")
    void nucleoNaoConheceHttp() throws IOException {
        List<Path> nucleo = arquivosJava(RAIZ.resolve("recomendacoes"), false);

        assertEquals(List.of(), violacoes(nucleo, List.of(
                "import org.springframework.web.",
                "import org.springframework.http.",
                "import java.net.")));
    }

    @Test
    @DisplayName("A implementacao do caso de uso nao e visivel fora do modulo")
    void casoDeUsoNaoEPublico() {
        assertFalse(Modifier.isPublic(RecomendacoesService.class.getModifiers()));
    }

    private static List<Path> arquivosJava(Path pasta, boolean recursivo) throws IOException {
        try (Stream<Path> arquivos = recursivo ? Files.walk(pasta) : Files.list(pasta)) {
            return arquivos.filter(p -> p.toString().endsWith(".java")).toList();
        }
    }

    private static List<String> violacoes(List<Path> arquivos, List<String> proibidos) throws IOException {
        List<String> encontradas = new ArrayList<>();
        for (Path arquivo : arquivos) {
            String fonte = Files.readString(arquivo);
            proibidos.stream()
                    .filter(fonte::contains)
                    .forEach(p -> encontradas.add(arquivo.getFileName() + " -> " + p));
        }
        return encontradas;
    }
}
