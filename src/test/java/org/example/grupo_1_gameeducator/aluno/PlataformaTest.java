package org.example.grupo_1_gameeducator.aluno;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlataformaTest {

    @Test
    @DisplayName("Dado que o aluno finalize o curso com média maior que 7,0, quando for resgatar seus 3 cursos, "
            + "então tenha a escolha de resgatar 3 entre 10 cursos relacionados ao que concluiu")
    void deveOferecerDezCursosRelacionadosParaEscolherTres() {
        Plataforma plataforma = new Plataforma();
        Aluno joao = new Aluno("João");
        Curso concluido = new Curso("Lógica de Programação", "Programação");
        plataforma.concluir(joao, concluido, 8.5);

        Resgate resgate = plataforma.iniciarResgate(joao);

        assertEquals(10, resgate.getCursosDisponiveis().size());
        assertEquals(3, resgate.getLimiteDeEscolhas());
        assertTrue(resgate.getCursosDisponiveis().stream().allMatch(c -> c.getArea().equals("Programação")));
    }

    @Test
    @DisplayName("Dado que o aluno finalize o curso com média igual ou menor que 7,0, quando tentar resgatar cursos, "
            + "então não tenha direito ao resgate")
    void naoDeveOferecerResgateQuandoMediaNaoUltrapassaSete() {
        Plataforma plataforma = new Plataforma();
        Aluno maria = new Aluno("Maria");
        Curso concluido = new Curso("Lógica de Programação", "Programação");
        plataforma.concluir(maria, concluido, 7.0);

        assertThrows(IllegalStateException.class, () -> plataforma.iniciarResgate(maria));
    }
}
