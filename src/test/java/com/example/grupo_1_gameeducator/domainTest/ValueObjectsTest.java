package com.example.grupo_1_gameeducator.domainTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.grupo_1_gameeducator.domain.vo.AreaCurso;
import com.example.grupo_1_gameeducator.domain.vo.DescricaoCurso;
import com.example.grupo_1_gameeducator.domain.vo.EmailAluno;
import com.example.grupo_1_gameeducator.domain.vo.NomeAluno;
import com.example.grupo_1_gameeducator.domain.vo.TituloCurso;
import java.lang.reflect.Constructor;
import org.junit.jupiter.api.Test;

class ValueObjectsTest {

    // Construtores do JPA, chamados por reflexao porque sao protegidos.
    private static <T> T instanciarComoJpa(Class<T> tipo) throws Exception {
        Constructor<T> construtor = tipo.getDeclaredConstructor();
        construtor.setAccessible(true);
        return construtor.newInstance();
    }

    @Test
    void nomeDeveTirarEspacos() {
        assertEquals("Luiza", new NomeAluno("  Luiza  ").getValor());
    }

    @Test
    void nomeVazioDeveSerRecusado() {
        assertThrows(IllegalArgumentException.class, () -> new NomeAluno(null));
        assertThrows(IllegalArgumentException.class, () -> new NomeAluno("   "));
    }

    @Test
    void emailDeveFicarEmMinusculo() {
        assertEquals("luiza@exemplo.com", new EmailAluno("  LUIZA@Exemplo.com ").getValor());
    }

    @Test
    void emailInvalidoDeveSerRecusado() {
        assertThrows(IllegalArgumentException.class, () -> new EmailAluno(null));
        assertThrows(IllegalArgumentException.class, () -> new EmailAluno("   "));
        assertThrows(IllegalArgumentException.class, () -> new EmailAluno("luiza.exemplo.com"));
    }

    @Test
    void tituloDeveTirarEspacos() {
        assertEquals("Banco de Dados", new TituloCurso(" Banco de Dados ").getValor());
    }

    @Test
    void tituloVazioDeveSerRecusado() {
        assertThrows(IllegalArgumentException.class, () -> new TituloCurso(null));
        assertThrows(IllegalArgumentException.class, () -> new TituloCurso(""));
    }

    @Test
    void descricaoDeveTirarEspacos() {
        assertEquals("Curso introdutorio", new DescricaoCurso(" Curso introdutorio ").getValor());
    }

    @Test
    void descricaoVaziaViraNulo() {
        assertNull(new DescricaoCurso(null).getValor());
        assertNull(new DescricaoCurso("   ").getValor());
    }

    @Test
    void areaDeveTirarEspacos() {
        assertEquals("Programacao", new AreaCurso(" Programacao ").getValor());
    }

    @Test
    void areaVaziaViraGeral() {
        assertEquals(AreaCurso.AREA_PADRAO, new AreaCurso(null).getValor());
        assertEquals("Geral", new AreaCurso("   ").getValor());
    }

    @Test
    void construtoresDoJpaDevemExistir() throws Exception {
        assertNotNull(instanciarComoJpa(NomeAluno.class));
        assertNotNull(instanciarComoJpa(EmailAluno.class));
        assertNotNull(instanciarComoJpa(TituloCurso.class));
        assertNotNull(instanciarComoJpa(DescricaoCurso.class));
        assertNotNull(instanciarComoJpa(AreaCurso.class));
    }
}
