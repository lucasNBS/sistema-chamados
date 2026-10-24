package br.edu.ifrn.chamados.exception;

import java.time.LocalDateTime;

public record ApiError(
    LocalDateTime timestamp,
    int status,
    String mensagem,
    String caminho
) {
}
