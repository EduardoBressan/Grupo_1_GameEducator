package org.example.grupo_1_gameeducator.aluno;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Plataforma {

    private static final double MEDIA_MINIMA_PARA_RESGATE = 7.0;
    private static final int QTD_CURSOS_DISPONIVEIS = 10;
    private static final int LIMITE_DE_ESCOLHAS = 3;

    private final List<Curso> catalogoDeCursos;
    private final Map<Aluno, Curso> ultimoCursoConcluido = new HashMap<>();

    public Plataforma() {
        this(catalogoPadrao());
    }

    public Plataforma(List<Curso> catalogoDeCursos) {
        this.catalogoDeCursos = catalogoDeCursos;
    }

    public void concluir(Aluno aluno, Curso curso, double nota) {
        aluno.setMediaCurso(nota);
        ultimoCursoConcluido.put(aluno, curso);
    }

    public Resgate iniciarResgate(Aluno aluno) {
        // STUB - ainda não implementado (fase RED)
        throw new UnsupportedOperationException("não implementado");
    }

    private static List<Curso> catalogoPadrao() {
        return List.of(
                new Curso("Estruturas de Dados", "Programação"),
                new Curso("Algoritmos Avançados", "Programação"),
                new Curso("Programação Orientada a Objetos", "Programação"),
                new Curso("Programação Funcional", "Programação"),
                new Curso("Desenvolvimento Web", "Programação"),
                new Curso("Desenvolvimento Mobile", "Programação"),
                new Curso("Banco de Dados", "Programação"),
                new Curso("Testes Automatizados", "Programação"),
                new Curso("Arquitetura de Software", "Programação"),
                new Curso("DevOps Essencial", "Programação"),
                new Curso("Fundamentos de Design", "Design"),
                new Curso("Marketing Digital", "Marketing")
        );
    }
}
