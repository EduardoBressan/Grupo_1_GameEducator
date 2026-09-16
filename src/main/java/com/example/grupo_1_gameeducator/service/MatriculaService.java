package com.example.grupo_1_gameeducator.service;

import com.example.grupo_1_gameeducator.domain.PlataformaEnsino;
import com.example.grupo_1_gameeducator.dto.CursoResponseDTO;
import com.example.grupo_1_gameeducator.dto.MatriculaResponseDTO;
import com.example.grupo_1_gameeducator.dto.ResgateResponseDTO;
import com.example.grupo_1_gameeducator.entity.AlunoEntity;
import com.example.grupo_1_gameeducator.entity.CursoEntity;
import com.example.grupo_1_gameeducator.entity.MatriculaEntity;
import com.example.grupo_1_gameeducator.entity.StatusMatricula;
import com.example.grupo_1_gameeducator.repository.CursoRepository;
import com.example.grupo_1_gameeducator.repository.MatriculaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Camada: SERVICE.
// Este service e o ponto onde a aplicacao web encontra as regras de negocio
// construidas por TDD na classe de dominio PlataformaEnsino. A regra em si
// (quem aprova cursos adicionais, quantos cursos relacionados oferecer) e
// sempre a mesma testada em domainTest - este service so traduz entidades
// persistidas para ela e devolve o resultado como DTO.
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

        // A decisao e a mesma regra testada por TDD em PlataformaEnsino: media
        // ACIMA de 7,0 concede 3 cursos adicionais - mesmo que o aluno ja tenha
        // usado os anteriores (cenario do Eduardo: o saldo e cumulativo, nao
        // trava em zero).
        int liberados = calcularCursosAdicionaisLiberados(mediaFinal);
        matricula.getAluno().liberarCursosAdicionais(liberados);

        return toDTO(matriculaRepository.save(matricula));
    }

    // Cenario do Felipe: ao resgatar, o aluno recebe ate 10 cursos da mesma
    // area do que concluiu (exceto ele mesmo) e pode escolher 3 - a mesma
    // regra de PlataformaEnsino.iniciarResgate, aplicada aqui sobre o catalogo
    // real de cursos cadastrados no banco.
    public ResgateResponseDTO iniciarResgate(Long matriculaId) {
        MatriculaEntity matricula = matriculaRepository.findById(matriculaId)
                .orElseThrow(() -> new RuntimeException("Matricula nao encontrada"));

        if (matricula.getStatus() != StatusMatricula.CONCLUIDO) {
            throw new IllegalStateException("Matricula ainda nao foi concluida.");
        }

        AlunoEntity aluno = matricula.getAluno();
        if (aluno.getCursosAdicionaisDisponiveis() <= 0) {
            throw new IllegalStateException("Aluno nao tem cursos adicionais para resgatar.");
        }

        CursoEntity cursoConcluido = matricula.getCurso();

        List<CursoResponseDTO> relacionados = cursoRepository
                .findByAreaAndIdNot(cursoConcluido.getArea(), cursoConcluido.getId())
                .stream()
                .limit(PlataformaEnsino.CURSOS_RELACIONADOS_OFERECIDOS)
                .map(this::toCursoDTO)
                .toList();

        return new ResgateResponseDTO(relacionados, PlataformaEnsino.CURSOS_ADICIONAIS_POR_APROVACAO);
    }

    // A regra em si (media ACIMA de 7,0) e a mesma testada em
    // PlataformaEnsinoTest / ElegibilidadeDeCursosAdicionaisTest.
    private int calcularCursosAdicionaisLiberados(Double mediaFinal) {
        if (mediaFinal == null) {
            return 0;
        }
        return PlataformaEnsino.aprovadoParaCursosAdicionais(mediaFinal)
                ? PlataformaEnsino.CURSOS_ADICIONAIS_POR_APROVACAO
                : 0;
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
                calcularCursosAdicionaisLiberados(matricula.getMediaFinal()),
                aluno.getCursosAdicionaisDisponiveis()
        );
    }

    private CursoResponseDTO toCursoDTO(CursoEntity curso) {
        return new CursoResponseDTO(curso.getId(), curso.getTitulo(), curso.getDescricao(), curso.getArea());
    }
}
