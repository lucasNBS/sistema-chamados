package br.edu.ifrn.chamados.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record InteracaoRequest(
    @NotNull @Positive Long autorId,
    @NotBlank String texto
) {
}
