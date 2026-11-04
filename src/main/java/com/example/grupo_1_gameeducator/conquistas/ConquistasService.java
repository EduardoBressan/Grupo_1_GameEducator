package com.example.grupo_1_gameeducator.conquistas;

import com.example.grupo_1_gameeducator.domain.Curso;
import com.example.grupo_1_gameeducator.domain.Matricula;
import com.example.grupo_1_gameeducator.domain.PlataformaEnsino;
import com.example.grupo_1_gameeducator.domain.Resgate;
import com.example.grupo_1_gameeducator.domain.StatusMatricula;
import org.springframework.stereotype.Service;

import java.util.List;

// Implementacao interna do modulo Conquistas. Nao e publica de proposito:
// fora do pacote so se enxerga a ConquistasApi (ver FronteiraConquistasTest).
// Quem decide as regras continua sendo o PlataformaEnsino, ja coberto pelos testes de dominio.
// Desafio 2 (ADR-002): depende da porta CatalogoDeCursos, e nao mais do repositorio JPA.
@Service
class ConquistasService implements ConquistasApi {

    private final CatalogoDeCursos catalogo;

    ConquistasService(CatalogoDeCursos catalogo) {
        this.catalogo = catalogo;
    }

    @Override
    public void concluirCurso(Matricula matricula, double mediaFinal) {
        new PlataformaEnsino().finalizarCurso(matricula, mediaFinal);
    }

    @Override
    public Resgate iniciarResgate(Matricula matricula) {
        if (matricula.getStatus() != StatusMatricula.CONCLUIDO) {
            throw new IllegalStateException("Matricula ainda nao foi concluida.");
        }

        List<Curso> mesmaArea = catalogo.relacionadosA(matricula.getCurso());

        return new PlataformaEnsino(mesmaArea).iniciarResgate(matricula);
    }

    @Override
    public int cursosAdicionaisLiberadosPor(Double mediaFinal) {
        if (mediaFinal == null) {
            return 0;
        }
        return PlataformaEnsino.aprovadoParaCursosAdicionais(mediaFinal)
                ? PlataformaEnsino.CURSOS_ADICIONAIS_POR_APROVACAO
                : 0;
    }
}
