package com.example.grupo_1_gameeducator.controller;

import com.example.grupo_1_gameeducator.dto.ConcluirMatriculaRequestDTO;
import com.example.grupo_1_gameeducator.dto.MatriculaRequestDTO;
import com.example.grupo_1_gameeducator.dto.MatriculaResponseDTO;
import com.example.grupo_1_gameeducator.dto.ResgateResponseDTO;
import com.example.grupo_1_gameeducator.service.MatriculaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/matriculas")
@Tag(name = "Matriculas")
public class MatriculaRestController {

    private final MatriculaService service;

    public MatriculaRestController(MatriculaService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Matricular aluno em um curso")
    public MatriculaResponseDTO matricular(@Valid @RequestBody MatriculaRequestDTO dto) {
        return service.matricular(dto.getAlunoId(), dto.getCursoId(), dto.isCursoAdicional());
    }

    @PutMapping("/{id}/concluir")
    @Operation(summary = "Concluir matricula com a media final e liberar cursos adicionais")
    public MatriculaResponseDTO concluir(@PathVariable Long id,
                                         @Valid @RequestBody ConcluirMatriculaRequestDTO dto) {
        return service.concluir(id, dto.getMediaFinal());
    }

    @GetMapping("/{id}/resgate")
    @Operation(summary = "Resgatar cursos relacionados ao curso concluido (escolher 3 entre ate 10)")
    public ResgateResponseDTO resgatar(@PathVariable Long id) {
        return service.iniciarResgate(id);
    }

    @GetMapping("/aluno/{alunoId}")
    @Operation(summary = "Listar matriculas de um aluno")
    public List<MatriculaResponseDTO> listarPorAluno(@PathVariable Long alunoId) {
        return service.listarPorAluno(alunoId);
    }
}
