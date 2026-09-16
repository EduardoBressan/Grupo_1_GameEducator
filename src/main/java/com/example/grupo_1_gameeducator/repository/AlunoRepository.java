package com.example.grupo_1_gameeducator.repository;

import com.example.grupo_1_gameeducator.domain.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;

// Camada: REPOSITORY (Spring Data JPA).
public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    // Metodo derivado do nome: o Spring Data gera a consulta sozinho.
    // O "_Valor" e necessario porque o e-mail agora e um Value Object embutido:
    // o caminho da propriedade e email.valor.
    boolean existsByEmail_Valor(String email);
}
