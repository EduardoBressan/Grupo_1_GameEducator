package com.example.grupo_1_gameeducator.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

// Camada: DTO. Entrada da API para concluir uma matricula com a media final.
public class ConcluirMatriculaRequestDTO {

    @NotNull(message = "mediaFinal e obrigatoria")
    @DecimalMin(value = "0.0", message = "Media minima 0")
    @DecimalMax(value = "10.0", message = "Media maxima 10")
    private Double mediaFinal;

    public ConcluirMatriculaRequestDTO() {
    }

    public Double getMediaFinal() {
        return mediaFinal;
    }

    public void setMediaFinal(Double mediaFinal) {
        this.mediaFinal = mediaFinal;
    }
}
