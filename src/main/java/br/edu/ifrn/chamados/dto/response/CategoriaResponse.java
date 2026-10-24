package br.edu.ifrn.chamados.dto.response;

public record CategoriaResponse(
    Long id,
    String nome,
    String descricao,
    Long setorId
) {
}
