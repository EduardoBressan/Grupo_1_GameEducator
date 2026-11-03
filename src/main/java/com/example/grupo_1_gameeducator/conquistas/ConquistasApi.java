package com.example.grupo_1_gameeducator.conquistas;

import com.example.grupo_1_gameeducator.domain.Matricula;
import com.example.grupo_1_gameeducator.domain.Resgate;

// Desafio 1 (ADR-001): contrato publico do modulo Conquistas.
// O resto do Core so conhece esta interface; a regra de gamificacao
// (media acima de 7,0 libera +3 cursos, resgate de 3 entre 10) fica dentro do modulo.
public interface ConquistasApi {

    // Conclui a matricula e libera os cursos adicionais, se o aluno tiver direito.
    void concluirCurso(Matricula matricula, double mediaFinal);

    // Cursos relacionados ao que o aluno concluiu, para ele escolher.
    Resgate iniciarResgate(Matricula matricula);

    // Quantos cursos adicionais uma media libera (0 se a matricula ainda nao tem media).
    int cursosAdicionaisLiberadosPor(Double mediaFinal);
}
