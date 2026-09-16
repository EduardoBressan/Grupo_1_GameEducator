package com.example.grupo_1_gameeducator.repository;

import com.example.grupo_1_gameeducator.domain.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    // O "_Valor" e porque o e-mail e um Value Object: o caminho e email.valor.
    boolean existsByEmail_Valor(String email);
}
