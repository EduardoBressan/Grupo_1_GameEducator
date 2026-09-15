package com.example.grupo_1_gameeducator.repository;

import com.example.grupo_1_gameeducator.entity.MatriculaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Camada: REPOSITORY (Spring Data JPA).
public interface MatriculaRepository extends JpaRepository<MatriculaEntity, Long> {

    List<MatriculaEntity> findByAlunoId(Long alunoId);
}
