package com.example.grupo_1_gameeducator.repository;

import com.example.grupo_1_gameeducator.domain.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Camada: REPOSITORY (Spring Data JPA).
public interface CursoRepository extends JpaRepository<Curso, Long> {

    // Cenario do Felipe: cursos relacionados = mesma area, exceto o que o aluno
    // acabou de concluir. O "_Valor" e o caminho da propriedade area.valor,
    // porque a area e um Value Object embutido.
    List<Curso> findByArea_ValorAndIdNot(String area, Long id);
}
