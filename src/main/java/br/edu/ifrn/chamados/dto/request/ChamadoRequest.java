package br.edu.ifrn.chamados.dto.request;

import br.edu.ifrn.chamados.model.enums.Prioridade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ChamadoRequest(
    @NotNull @Positive Long solicitanteId,
    @NotNull @Positive Long categoriaId,
    @NotBlank @Size(max = 150) String titulo,
    @NotBlank String descricao,
    @NotBlank @Size(max = 150) String local,
    Prioridade prioridade
) {
}
