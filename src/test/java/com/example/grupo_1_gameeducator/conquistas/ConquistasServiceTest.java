package com.example.grupo_1_gameeducator.conquistas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.grupo_1_gameeducator.conquistas.adapter.memoria.CatalogoDeCursosEmMemoria;
import com.example.grupo_1_gameeducator.domain.Aluno;
import com.example.grupo_1_gameeducator.domain.Curso;
import com.example.grupo_1_gameeducator.domain.Matricula;
import com.example.grupo_1_gameeducator.domain.Resgate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// Desafio 1 (ADR-001): o modulo Conquistas aplica as mesmas regras do dominio da AC1.
// Desafio 2 (ADR-002): o caso de uso roda com o adapter em memoria no lugar do JPA,
// entao o teste nao sobe Spring, nao usa banco e nao precisa mais de mock do repositorio.
class ConquistasServiceTest {

    private CatalogoDeCursosEmMemoria catalogo;
    private ConquistasService conquistas;
    private Aluno aluno;
    private Curso cursoConcluido;

    @BeforeEach
    void setUp() {
        catalogo = new CatalogoDeCursosEmMemoria();
        conquistas = new ConquistasService(catalogo);
        aluno = new Aluno("Ana");
        cursoConcluido = new Curso("Logica de Programacao", "Curso base", "Programacao");
        catalogo.adicionar(cursoConcluido);
    }

    @Test
    @DisplayName("Media acima de 7,0 libera 3 cursos adicionais")
    void mediaAcimaDeSeteLiberaTresCursos() {
        Matricula matricula = new Matricula(aluno, cursoConcluido);

        conquistas.concluirCurso(matricula, 8.5);

        assertEquals(3, aluno.getCursosAdicionaisDisponiveis());
        assertEquals(8.5, matricula.getMediaFinal());
    }

    @Test
    @DisplayName("Media exatamente 7,0 nao libera cursos adicionais")
    void mediaSeteNaoLiberaCursos() {
        Matricula matricula = new Matricula(aluno, cursoConcluido);

        conquistas.concluirCurso(matricula, 7.0);

        assertEquals(0, aluno.getCursosAdicionaisDisponiveis());
    }

    @Test
    @DisplayName("Calcula os cursos liberados por media, inclusive sem media")
    void calculaCursosLiberadosPorMedia() {
        assertEquals(3, conquistas.cursosAdicionaisLiberadosPor(9.0));
        assertEquals(0, conquistas.cursosAdicionaisLiberadosPor(7.0));
        assertEquals(0, conquistas.cursosAdicionaisLiberadosPor(null));
    }

    @Test
    @DisplayName("Resgate oferece no maximo 10 cursos da mesma area para escolher 3")
    void resgateOfereceDezCursosParaEscolherTres() {
        for (int i = 1; i <= 12; i++) {
            catalogo.adicionar(new Curso("Curso " + i, "Curso de programacao", "Programacao"));
        }
        catalogo.adicionar(new Curso("Fundamentos de Design", "Curso de design", "Design"));

        Matricula matricula = new Matricula(aluno, cursoConcluido);
        conquistas.concluirCurso(matricula, 8.5);

        Resgate resgate = conquistas.iniciarResgate(matricula);

        assertEquals(10, resgate.getCursosDisponiveis().size());
        assertEquals(3, resgate.getLimiteDeEscolhas());
    }

    @Test
    @DisplayName("Nao permite resgate de matricula que ainda nao foi concluida")
    void naoPermiteResgateDeMatriculaEmAndamento() {
        Matricula matricula = new Matricula(aluno, cursoConcluido);

        assertThrows(IllegalStateException.class, () -> conquistas.iniciarResgate(matricula));
    }

    @Test
    @DisplayName("Nao permite resgate se a media nao liberou cursos adicionais")
    void naoPermiteResgateSemCursosAdicionais() {
        Matricula matricula = new Matricula(aluno, cursoConcluido);
        conquistas.concluirCurso(matricula, 6.0);

        assertThrows(IllegalStateException.class, () -> conquistas.iniciarResgate(matricula));
    }
}
