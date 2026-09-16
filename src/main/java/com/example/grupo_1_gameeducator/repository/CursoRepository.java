package com.example.grupo_1_gameeducator.repository;

import com.example.grupo_1_gameeducator.entity.CursoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Camada: REPOSITORY (Spring Data JPA).
public interface CursoRepository extends JpaRepository<CursoEntity, Long> {

    // Cenario do Felipe: cursos relacionados = mesma area, exceto o que o aluno acabou de concluir.
    List<CursoEntity> findByAreaAndIdNot(String area, Long id);
}
