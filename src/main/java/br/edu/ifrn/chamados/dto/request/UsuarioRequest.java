package br.edu.ifrn.chamados.dto.request;

import br.edu.ifrn.chamados.model.enums.TipoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(
    @NotBlank @Size(max = 150) String nome,
    @NotBlank @Email @Size(max = 150) String email,
    @NotBlank @Size(max = 30) String matricula,
    @NotNull TipoUsuario tipo,
    @Positive Long setorId
) {
}
