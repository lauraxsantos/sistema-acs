package br.upe.acs.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AcsExcecao.class)
    public ResponseEntity<Object> handleAcsException(AcsExcecao ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(CepInvalidoExcecao.class)
    public ResponseEntity<Object> handleInvalidCepException(CepInvalidoExcecao ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(ex.getMessage());
    }

    @ExceptionHandler(FormatoInvalidoExcecao.class)
    public ResponseEntity<Object> handleInvalidFileFormatException(FormatoInvalidoExcecao ex) {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(ex.getMessage());
    }

    @ExceptionHandler(ConverterArquivoExcecao.class)
    public ResponseEntity<Object> handleConvertFileException(ConverterArquivoExcecao ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ex.getMessage());
    }
}
