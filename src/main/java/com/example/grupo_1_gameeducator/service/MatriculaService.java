package com.example.grupo_1_gameeducator.service;

import com.example.grupo_1_gameeducator.domain.Aluno;
import com.example.grupo_1_gameeducator.domain.Curso;
import com.example.grupo_1_gameeducator.domain.Matricula;
import com.example.grupo_1_gameeducator.domain.PlataformaEnsino;
import com.example.grupo_1_gameeducator.dto.MatriculaResponseDTO;
import com.example.grupo_1_gameeducator.entity.AlunoEntity;
import com.example.grupo_1_gameeducator.entity.CursoEntity;
import com.example.grupo_1_gameeducator.entity.MatriculaEntity;
import com.example.grupo_1_gameeducator.repository.CursoRepository;
import com.example.grupo_1_gameeducator.repository.MatriculaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Camada: SERVICE.
// Este service e o ponto onde a aplicacao web encontra a regra de negocio
// construida por TDD: quem decide quantos cursos adicionais a conclusao libera
// e a classe de dominio PlataformaEnsino, nao este service.
@Service
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final CursoRepository cursoRepository;
    private final AlunoService alunoService;

    // A regra de dominio nao guarda estado entre requisicoes.
    private final PlataformaEnsino plataformaEnsino = new PlataformaEnsino();

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
        AlunoEntity aluno = alunoService.buscarEntidade(alunoId);

        CursoEntity curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new RuntimeException("Curso nao encontrado"));

        // Se a matricula usa um dos cursos adicionais conquistados, consome um do saldo.
        if (cursoAdicional) {
            aluno.consumirCursoAdicional();
        }

        return toDTO(matriculaRepository.save(new MatriculaEntity(aluno, curso, cursoAdicional)));
    }

    @Transactional
    public MatriculaResponseDTO concluir(Long matriculaId, Double mediaFinal) {
        MatriculaEntity matricula = matriculaRepository.findById(matriculaId)
                .orElseThrow(() -> new RuntimeException("Matricula nao encontrada"));

        matricula.concluirCom(mediaFinal);

        // A decisao e do dominio testado por TDD.
        int liberados = calcularCursosAdicionaisLiberados(matricula.getAluno(), matricula.getCurso(), mediaFinal);
        matricula.getAluno().liberarCursosAdicionais(liberados);

        return toDTO(matriculaRepository.save(matricula));
    }

    // Traduz as entidades persistidas para os objetos de dominio e pergunta ao
    // PlataformaEnsino quantos cursos adicionais aquela media libera.
    private int calcularCursosAdicionaisLiberados(AlunoEntity alunoEntity, CursoEntity cursoEntity, Double mediaFinal) {
        if (mediaFinal == null) {
            return 0;
        }

        Aluno aluno = new Aluno(alunoEntity.getNome());
        Curso curso = new Curso(cursoEntity.getTitulo());

        Matricula matriculaDominio = plataformaEnsino.matricular(aluno, curso);
        plataformaEnsino.finalizarCurso(matriculaDominio, mediaFinal);

        return Math.max(plataformaEnsino.cursosAdicionaisLiberadosPara(aluno), 0);
    }

    // Mapeamento manual entidade -> DTO.
    private MatriculaResponseDTO toDTO(MatriculaEntity matricula) {
        AlunoEntity aluno = matricula.getAluno();
        return new MatriculaResponseDTO(
                matricula.getId(),
                aluno.getId(),
                aluno.getNome(),
                matricula.getCurso().getId(),
                matricula.getCurso().getTitulo(),
                matricula.getStatus().name(),
                matricula.getMediaFinal(),
                matricula.isCursoAdicional(),
                calcularCursosAdicionaisLiberados(aluno, matricula.getCurso(), matricula.getMediaFinal()),
                aluno.getCursosAdicionaisDisponiveis()
        );
    }
}
