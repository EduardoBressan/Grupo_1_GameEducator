package com.example.grupo_1_gameeducator.repository;

import com.example.grupo_1_gameeducator.entity.AlunoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

// Camada: REPOSITORY (Spring Data JPA).
public interface AlunoRepository extends JpaRepository<AlunoEntity, Long> {

    // Metodo derivado do nome: o Spring Data gera a consulta sozinho.
    boolean existsByEmail(String email);
}
