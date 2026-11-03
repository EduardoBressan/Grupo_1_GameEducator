package com.example.grupo_1_gameeducator.service;

import com.example.grupo_1_gameeducator.conquistas.ConquistasApi;
import com.example.grupo_1_gameeducator.domain.Aluno;
import com.example.grupo_1_gameeducator.domain.Curso;
import com.example.grupo_1_gameeducator.domain.Matricula;
import com.example.grupo_1_gameeducator.domain.Resgate;
import com.example.grupo_1_gameeducator.dto.CursoResponseDTO;
import com.example.grupo_1_gameeducator.dto.MatriculaResponseDTO;
import com.example.grupo_1_gameeducator.dto.ResgateResponseDTO;
import com.example.grupo_1_gameeducator.repository.CursoRepository;
import com.example.grupo_1_gameeducator.repository.MatriculaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// As regras de gamificacao ficam no modulo Conquistas (ADR-001);
// aqui so se usa o contrato ConquistasApi.
@Service
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final CursoRepository cursoRepository;
    private final AlunoService alunoService;
    private final ConquistasApi conquistas;

    public MatriculaService(MatriculaRepository matriculaRepository,
                            CursoRepository cursoRepository,
                            AlunoService alunoService,
                            ConquistasApi conquistas) {
        this.matriculaRepository = matriculaRepository;
        this.cursoRepository = cursoRepository;
        this.alunoService = alunoService;
        this.conquistas = conquistas;
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

        conquistas.concluirCurso(matricula, mediaFinal);

        return toDTO(matriculaRepository.save(matricula));
    }

    // Cursos da mesma area do que o aluno concluiu, para ele escolher 3.
    public ResgateResponseDTO iniciarResgate(Long matriculaId) {
        Matricula matricula = matriculaRepository.findById(matriculaId)
                .orElseThrow(() -> new RuntimeException("Matricula nao encontrada"));

        Resgate resgate = conquistas.iniciarResgate(matricula);

        List<CursoResponseDTO> relacionados = resgate.getCursosDisponiveis().stream()
                .map(this::toCursoDTO)
                .toList();

        return new ResgateResponseDTO(relacionados, resgate.getLimiteDeEscolhas());
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
                conquistas.cursosAdicionaisLiberadosPor(matricula.getMediaFinal()),
                aluno.getCursosAdicionaisDisponiveis()
        );
    }

    private CursoResponseDTO toCursoDTO(Curso curso) {
        return new CursoResponseDTO(curso.getId(), curso.getTitulo(), curso.getDescricao(), curso.getArea());
    }
}
