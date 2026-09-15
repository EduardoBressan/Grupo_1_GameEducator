package com.example.grupo_1_gameeducator.controller;

import com.example.grupo_1_gameeducator.dto.CursoRequestDTO;
import com.example.grupo_1_gameeducator.dto.CursoResponseDTO;
import com.example.grupo_1_gameeducator.service.CursoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Camada: CONTROLLER.
@RestController
@RequestMapping("/api/cursos")
@Tag(name = "Cursos")
public class CursoRestController {

    private final CursoService service;

    public CursoRestController(CursoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar cursos")
    public List<CursoResponseDTO> listar() {
        return service.listarTodos();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar curso")
    public CursoResponseDTO criar(@Valid @RequestBody CursoRequestDTO dto) {
        return service.criar(dto);
    }
}
