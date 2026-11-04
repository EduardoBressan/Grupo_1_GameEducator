package com.example.grupo_1_gameeducator.conquistas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.grupo_1_gameeducator.repository.CursoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

// Desafio 2 (ADR-002): teste de arquitetura de Ports & Adapters.
// As dependencias de infraestrutura so podem aparecer nos adapters, nunca no nucleo do modulo
// nem no dominio. Se alguem importar o repositorio direto no caso de uso, este teste falha.
class FronteiraHexagonalTest {

    private static final Path RAIZ = Path.of("src/main/java/com/example/grupo_1_gameeducator");

    private static final List<String> IMPORTS_DE_INFRAESTRUTURA = List.of(
            "import com.example.grupo_1_gameeducator.repository.",
            "import org.springframework.data.",
            "import jakarta.persistence.");

    @Test
    @DisplayName("O nucleo do modulo Conquistas nao importa repositorio, Spring Data nem JPA")
    void nucleoDoModuloNaoImportaInfraestrutura() throws IOException {
        List<String> violacoes = new ArrayList<>();

        try (Stream<Path> arquivos = Files.list(RAIZ.resolve("conquistas"))) {
            for (Path arquivo : arquivos.filter(p -> p.toString().endsWith(".java")).toList()) {
                String fonte = Files.readString(arquivo);
                IMPORTS_DE_INFRAESTRUTURA.stream()
                        .filter(fonte::contains)
                        .forEach(imp -> violacoes.add(arquivo.getFileName() + " -> " + imp));
            }
        }

        assertEquals(List.of(), violacoes);
    }

    @Test
    @DisplayName("O caso de uso recebe a porta de saida, nao o repositorio")
    void casoDeUsoRecebeAPorta() {
        List<Class<?>> parametros = Arrays.stream(ConquistasService.class.getDeclaredConstructors())
                .flatMap(construtor -> Arrays.stream(construtor.getParameterTypes()))
                .toList();

        assertTrue(parametros.contains(CatalogoDeCursos.class));
        assertFalse(parametros.contains(CursoRepository.class));
    }

    @Test
    @DisplayName("A porta de saida e uma interface publica do modulo")
    void portaDeSaidaEInterfacePublica() {
        assertTrue(CatalogoDeCursos.class.isInterface());
        assertTrue(Modifier.isPublic(CatalogoDeCursos.class.getModifiers()));
    }

    @Test
    @DisplayName("O dominio nao depende de Spring, dos repositorios nem dos modulos de aplicacao")
    void dominioNaoDependeDeFrameworkNemDeCamadasExternas() throws IOException {
        List<String> proibidos = List.of(
                "import org.springframework.",
                "import com.example.grupo_1_gameeducator.repository.",
                "import com.example.grupo_1_gameeducator.service.",
                "import com.example.grupo_1_gameeducator.conquistas.");
        List<String> violacoes = new ArrayList<>();

        try (Stream<Path> arquivos = Files.walk(RAIZ.resolve("domain"))) {
            for (Path arquivo : arquivos.filter(p -> p.toString().endsWith(".java")).toList()) {
                String fonte = Files.readString(arquivo);
                proibidos.stream()
                        .filter(fonte::contains)
                        .forEach(imp -> violacoes.add(arquivo.getFileName() + " -> " + imp));
            }
        }

        assertEquals(List.of(), violacoes);
    }
}
