package br.edu.ifrn.chamados.dto.response;

import br.edu.ifrn.chamados.model.enums.Prioridade;
import br.edu.ifrn.chamados.model.enums.StatusChamado;

import java.time.LocalDateTime;

public record ChamadoResponse(
    Long id,
    String titulo,
    String descricao,
    String local,
    Prioridade prioridade,
    StatusChamado status,
    LocalDateTime dataAbertura,
    LocalDateTime dataFechamento,
    String solucao,
    Long solicitanteId,
    Long categoriaId,
    Long tecnicoId
) {
}
