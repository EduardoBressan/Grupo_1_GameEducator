package com.example.grupo_1_gameeducator.conquistas;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.grupo_1_gameeducator.service.MatriculaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

// Desafio 1 (ADR-001): teste de arquitetura do modulo Conquistas.
// Garante que a fronteira do modulo continua valendo depois de futuras mudancas.
class FronteiraConquistasTest {

    private static final Path FONTE_MATRICULA_SERVICE = Path.of(
            "src/main/java/com/example/grupo_1_gameeducator/service/MatriculaService.java");

    @Test
    @DisplayName("O contrato do modulo e publico")
    void contratoDoModuloEPublico() {
        assertTrue(ConquistasApi.class.isInterface());
        assertTrue(Modifier.isPublic(ConquistasApi.class.getModifiers()));
    }

    @Test
    @DisplayName("A implementacao do modulo nao e visivel fora do pacote conquistas")
    void implementacaoNaoEPublica() {
        assertFalse(Modifier.isPublic(ConquistasService.class.getModifiers()));
    }

    @Test
    @DisplayName("O MatriculaService recebe o modulo pelo contrato, nao pela implementacao")
    void matriculaServiceDependeDoContrato() {
        boolean recebeContrato = Arrays.stream(MatriculaService.class.getConstructors())
                .flatMap(construtor -> Arrays.stream(construtor.getParameterTypes()))
                .anyMatch(tipo -> tipo.equals(ConquistasApi.class));

        assertTrue(recebeContrato);
    }

    @Test
    @DisplayName("A regra de gamificacao nao aparece mais no MatriculaService")
    void regraNaoEstaDuplicadaNoMatriculaService() throws IOException {
        String fonte = Files.readString(FONTE_MATRICULA_SERVICE);

        assertFalse(fonte.contains("PlataformaEnsino"));
        assertFalse(fonte.contains("StatusMatricula"));
        assertFalse(fonte.contains("liberarCursosAdicionais"));
        assertFalse(fonte.contains("CURSOS_ADICIONAIS_POR_APROVACAO"));
        assertFalse(fonte.contains("CURSOS_RELACIONADOS_OFERECIDOS"));
    }
}
