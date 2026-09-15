package com.example.grupo_1_gameeducator.controller;

import com.example.grupo_1_gameeducator.dto.AlunoRequestDTO;
import com.example.grupo_1_gameeducator.dto.AlunoResponseDTO;
import com.example.grupo_1_gameeducator.service.AlunoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Camada: CONTROLLER. Expoe os endpoints HTTP de aluno.
@RestController
@RequestMapping("/api/alunos")
@Tag(name = "Alunos")
public class AlunoRestController {

    private final AlunoService service;

    public AlunoRestController(AlunoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar alunos")
    public List<AlunoResponseDTO> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar aluno por id")
    public AlunoResponseDTO buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar aluno com assinatura basica")
    public AlunoResponseDTO criar(@Valid @RequestBody AlunoRequestDTO dto) {
        return service.criar(dto);
    }
}
