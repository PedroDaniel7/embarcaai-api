package br.com.heaven.embarcaai.handler;

import br.com.heaven.embarcaai.exception.NaoEncontradoException;
import br.com.heaven.embarcaai.exception.RegraNegocioException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);
    public record ErroDTO(int status, String mensagem, LocalDateTime momento) {}

    private ResponseEntity<Object> responder(HttpStatusCode status, String mensagem) {
        return ResponseEntity.status(status).body(new ErroDTO(status.value(), mensagem, LocalDateTime.now()));
    }

    @ExceptionHandler(NaoEncontradoException.class)
    public ResponseEntity<Object> naoEncontrado(NaoEncontradoException ex) {
        return responder(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<Object> regraNegocio(RegraNegocioException ex) {
        return responder(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> idInvalido(MethodArgumentTypeMismatchException ex) {
        return responder(HttpStatus.BAD_REQUEST, "Parametro da URL invalido");
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        String erro = ex.getBindingResult().getFieldErrors().stream().findFirst()
                .map(e -> e.getField() + ": " + e.getDefaultMessage()).orElse("Dados invalidos");
        return responder(HttpStatus.BAD_REQUEST, erro);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        return responder(HttpStatus.BAD_REQUEST, "JSON malformado ou valor invalido");
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        return responder(HttpStatus.METHOD_NOT_ALLOWED, "Metodo HTTP nao permitido");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> desconhecido(Exception ex) {
        // O detalhe vai so para o log do servidor; o JSON continua sem senha, SQL ou stack trace.
        log.error("Erro interno nao tratado", ex);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor");
    }
}
