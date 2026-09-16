package com.example.grupo_1_gameeducator.service;

import com.example.grupo_1_gameeducator.domain.Aluno;
import com.example.grupo_1_gameeducator.dto.AlunoRequestDTO;
import com.example.grupo_1_gameeducator.dto.AlunoResponseDTO;
import com.example.grupo_1_gameeducator.repository.AlunoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AlunoService {

    private final AlunoRepository repository;

    public AlunoService(AlunoRepository repository) {
        this.repository = repository;
    }

    public List<AlunoResponseDTO> listarTodos() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    public AlunoResponseDTO buscarPorId(Long id) {
        return toDTO(buscarEntidade(id));
    }

    @Transactional
    public AlunoResponseDTO criar(AlunoRequestDTO dto) {
        String emailNormalizado = dto.getEmail().trim().toLowerCase();

        if (repository.existsByEmail_Valor(emailNormalizado)) {
            throw new RuntimeException("E-mail ja cadastrado");
        }

        return toDTO(repository.save(new Aluno(dto.getNome(), emailNormalizado)));
    }

    public Aluno buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno nao encontrado"));
    }

    private AlunoResponseDTO toDTO(Aluno aluno) {
        return new AlunoResponseDTO(
                aluno.getId(),
                aluno.getNome(),
                aluno.getEmail(),
                aluno.getCursosAdicionaisDisponiveis()
        );
    }
}
