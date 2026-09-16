package com.example.grupo_1_gameeducator.service;

import com.example.grupo_1_gameeducator.dto.CursoRequestDTO;
import com.example.grupo_1_gameeducator.dto.CursoResponseDTO;
import com.example.grupo_1_gameeducator.entity.CursoEntity;
import com.example.grupo_1_gameeducator.repository.CursoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Camada: SERVICE.
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
        CursoEntity curso = new CursoEntity(dto.getTitulo(), dto.getDescricao(), dto.getArea());
        return toDTO(repository.save(curso));
    }

    // Mapeamento manual entidade -> DTO.
    private CursoResponseDTO toDTO(CursoEntity curso) {
        return new CursoResponseDTO(curso.getId(), curso.getTitulo(), curso.getDescricao(), curso.getArea());
    }
}
