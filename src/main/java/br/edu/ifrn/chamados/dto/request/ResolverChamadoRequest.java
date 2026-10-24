package br.edu.ifrn.chamados.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ResolverChamadoRequest(
    @NotNull @Positive Long tecnicoId,
    @NotBlank String solucao
) {
}
