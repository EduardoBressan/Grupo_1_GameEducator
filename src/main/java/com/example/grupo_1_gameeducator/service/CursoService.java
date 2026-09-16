package com.example.grupo_1_gameeducator.service;

import com.example.grupo_1_gameeducator.domain.Curso;
import com.example.grupo_1_gameeducator.dto.CursoRequestDTO;
import com.example.grupo_1_gameeducator.dto.CursoResponseDTO;
import com.example.grupo_1_gameeducator.repository.CursoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CursoService {

    private final CursoRepository repository;

    public CursoService(CursoRepository repository) {
        this.repository = repository;
    }

    public List<CursoResponseDTO> listarTodos() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional
    public CursoResponseDTO criar(CursoRequestDTO dto) {
        Curso curso = new Curso(dto.getTitulo(), dto.getDescricao(), dto.getArea());
        return toDTO(repository.save(curso));
    }

    private CursoResponseDTO toDTO(Curso curso) {
        return new CursoResponseDTO(curso.getId(), curso.getTitulo(), curso.getDescricao(), curso.getArea());
    }
}
