package br.edu.ifrn.chamados.dto.response;

import java.util.List;

public record ChamadoDetalheResponse(
    ChamadoResponse chamado,
    List<InteracaoResponse> interacoes
) {
}
