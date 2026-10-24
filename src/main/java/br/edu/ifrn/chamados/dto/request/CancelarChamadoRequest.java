package br.edu.ifrn.chamados.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CancelarChamadoRequest(
    @NotNull @Positive Long solicitanteId
) {
}
