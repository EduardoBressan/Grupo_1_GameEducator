package com.example.grupo_1_gameeducator.service;

import com.example.grupo_1_gameeducator.domain.Aluno;
import com.example.grupo_1_gameeducator.domain.Curso;
import com.example.grupo_1_gameeducator.domain.Matricula;
import com.example.grupo_1_gameeducator.domain.PlataformaEnsino;
import com.example.grupo_1_gameeducator.domain.StatusMatricula;
import com.example.grupo_1_gameeducator.dto.CursoResponseDTO;
import com.example.grupo_1_gameeducator.dto.MatriculaResponseDTO;
import com.example.grupo_1_gameeducator.dto.ResgateResponseDTO;
import com.example.grupo_1_gameeducator.repository.CursoRepository;
import com.example.grupo_1_gameeducator.repository.MatriculaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Quem decide as regras e o PlataformaEnsino, do pacote domain.
@Service
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final CursoRepository cursoRepository;
    private final AlunoService alunoService;

    public MatriculaService(MatriculaRepository matriculaRepository,
                            CursoRepository cursoRepository,
                            AlunoService alunoService) {
        this.matriculaRepository = matriculaRepository;
        this.cursoRepository = cursoRepository;
        this.alunoService = alunoService;
    }

    public List<MatriculaResponseDTO> listarPorAluno(Long alunoId) {
        return matriculaRepository.findByAlunoId(alunoId).stream().map(this::toDTO).toList();
    }

    @Transactional
    public MatriculaResponseDTO matricular(Long alunoId, Long cursoId, boolean cursoAdicional) {
        Aluno aluno = alunoService.buscarEntidade(alunoId);

        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new RuntimeException("Curso nao encontrado"));

        if (cursoAdicional) {
            aluno.consumirCursoAdicional();
        }

        return toDTO(matriculaRepository.save(new Matricula(aluno, curso, cursoAdicional)));
    }

    @Transactional
    public MatriculaResponseDTO concluir(Long matriculaId, Double mediaFinal) {
        Matricula matricula = matriculaRepository.findById(matriculaId)
                .orElseThrow(() -> new RuntimeException("Matricula nao encontrada"));

        matricula.concluirCom(mediaFinal);
        matricula.getAluno().liberarCursosAdicionais(calcularCursosAdicionaisLiberados(mediaFinal));

        return toDTO(matriculaRepository.save(matricula));
    }

    // Cursos da mesma area do que o aluno concluiu, para ele escolher 3.
    public ResgateResponseDTO iniciarResgate(Long matriculaId) {
        Matricula matricula = matriculaRepository.findById(matriculaId)
                .orElseThrow(() -> new RuntimeException("Matricula nao encontrada"));

        if (matricula.getStatus() != StatusMatricula.CONCLUIDO) {
            throw new IllegalStateException("Matricula ainda nao foi concluida.");
        }

        Aluno aluno = matricula.getAluno();
        if (aluno.getCursosAdicionaisDisponiveis() <= 0) {
            throw new IllegalStateException("Aluno nao tem cursos adicionais para resgatar.");
        }

        Curso cursoConcluido = matricula.getCurso();

        List<CursoResponseDTO> relacionados = cursoRepository
                .findByArea_ValorAndIdNot(cursoConcluido.getArea(), cursoConcluido.getId())
                .stream()
                .limit(PlataformaEnsino.CURSOS_RELACIONADOS_OFERECIDOS)
                .map(this::toCursoDTO)
                .toList();

        return new ResgateResponseDTO(relacionados, PlataformaEnsino.CURSOS_ADICIONAIS_POR_APROVACAO);
    }

    private int calcularCursosAdicionaisLiberados(Double mediaFinal) {
        if (mediaFinal == null) {
            return 0;
        }
        return PlataformaEnsino.aprovadoParaCursosAdicionais(mediaFinal)
                ? PlataformaEnsino.CURSOS_ADICIONAIS_POR_APROVACAO
                : 0;
    }

    private MatriculaResponseDTO toDTO(Matricula matricula) {
        Aluno aluno = matricula.getAluno();
        return new MatriculaResponseDTO(
                matricula.getId(),
                aluno.getId(),
                aluno.getNome(),
                matricula.getCurso().getId(),
                matricula.getCurso().getTitulo(),
                matricula.getStatus().name(),
                matricula.getMediaFinal(),
                matricula.isCursoAdicional(),
                calcularCursosAdicionaisLiberados(matricula.getMediaFinal()),
                aluno.getCursosAdicionaisDisponiveis()
        );
    }

    private CursoResponseDTO toCursoDTO(Curso curso) {
        return new CursoResponseDTO(curso.getId(), curso.getTitulo(), curso.getDescricao(), curso.getArea());
    }
}
