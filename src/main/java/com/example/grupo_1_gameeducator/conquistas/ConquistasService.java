package com.example.grupo_1_gameeducator.conquistas;

import com.example.grupo_1_gameeducator.domain.Curso;
import com.example.grupo_1_gameeducator.domain.Matricula;
import com.example.grupo_1_gameeducator.domain.PlataformaEnsino;
import com.example.grupo_1_gameeducator.domain.Resgate;
import com.example.grupo_1_gameeducator.domain.StatusMatricula;
import com.example.grupo_1_gameeducator.repository.CursoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

// Implementacao interna do modulo Conquistas. Nao e publica de proposito:
// fora do pacote so se enxerga a ConquistasApi (ver FronteiraConquistasTest).
// Quem decide as regras continua sendo o PlataformaEnsino, ja coberto pelos testes de dominio.
@Service
class ConquistasService implements ConquistasApi {

    private final CursoRepository cursoRepository;

    ConquistasService(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
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

        Curso cursoConcluido = matricula.getCurso();
        List<Curso> mesmaArea = cursoRepository
                .findByArea_ValorAndIdNot(cursoConcluido.getArea(), cursoConcluido.getId());

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
