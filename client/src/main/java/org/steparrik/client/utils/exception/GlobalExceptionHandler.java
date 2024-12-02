package org.steparrik.client.utils.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.steparrik.client.utils.exception.ExceptionEntity;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidateTransactionException.class)
    public ResponseEntity<?> handleValidationExceptions(ValidateTransactionException ex) {
        ExceptionEntity exceptionEntity = new ExceptionEntity();
        exceptionEntity.setMessage(ex.getMessage());
        exceptionEntity.setLocalDateTime(ex.getTimestamp());
        return ResponseEntity.status(ex.getStatus()).body(exceptionEntity);
    }


}
