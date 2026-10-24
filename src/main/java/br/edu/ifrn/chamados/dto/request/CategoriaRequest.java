package br.edu.ifrn.chamados.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CategoriaRequest(
    @NotBlank @Size(max = 100) String nome,
    String descricao,
    @NotNull @Positive Long setorId
) {
}
