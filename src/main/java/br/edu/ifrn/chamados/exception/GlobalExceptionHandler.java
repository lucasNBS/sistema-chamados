package br.edu.ifrn.chamados.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ApiError> notFound(
        RecursoNaoEncontradoException ex,
        HttpServletRequest request
    ) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ApiError> conflict(
        RegraNegocioException ex,
        HttpServletRequest request
    ) {
        return error(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> integrity(
        DataIntegrityViolationException ex,
        HttpServletRequest request
    ) {
        return error(HttpStatus.CONFLICT, "Operação viola uma restrição de integridade dos dados", request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> invalid(
        MethodArgumentNotValidException ex,
        HttpServletRequest request
    ) {
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .distinct()
            .collect(Collectors.joining("; "));
        return error(HttpStatus.BAD_REQUEST, message.isBlank() ? "Dados inválidos" : message, request);
    }

    @ExceptionHandler({
        IllegalArgumentException.class,
        HttpMessageNotReadableException.class,
        MethodArgumentTypeMismatchException.class,
        TypeMismatchException.class
    })
    public ResponseEntity<ApiError> badRequest(
        Exception ex,
        HttpServletRequest request
    ) {
        return error(HttpStatus.BAD_REQUEST, "Parâmetro ou corpo da requisição inválido", request);
    }

    private ResponseEntity<ApiError> error(
        HttpStatus status,
        String message,
        HttpServletRequest request
    ) {
        ApiError body = new ApiError(LocalDateTime.now(), status.value(), message, request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
