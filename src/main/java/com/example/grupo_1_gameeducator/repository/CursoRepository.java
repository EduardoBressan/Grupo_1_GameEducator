package com.example.grupo_1_gameeducator.repository;

import com.example.grupo_1_gameeducator.domain.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CursoRepository extends JpaRepository<Curso, Long> {

    // Cursos da mesma area, menos o que o aluno acabou de concluir.
    List<Curso> findByArea_ValorAndIdNot(String area, Long id);
}
