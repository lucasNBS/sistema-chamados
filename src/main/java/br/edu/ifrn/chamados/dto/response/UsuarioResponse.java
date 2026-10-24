package br.edu.ifrn.chamados.dto.response;

import br.edu.ifrn.chamados.model.enums.TipoUsuario;

public record UsuarioResponse(
    Long id,
    String nome,
    String email,
    String matricula,
    TipoUsuario tipo,
    Long setorId
) {
}
