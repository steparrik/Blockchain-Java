package org.steparrik.blockchain.utils.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class TcpException extends RuntimeException{
    private final LocalDateTime timestamp;

    public TcpException(String message) {
        super(message);
        this.timestamp = LocalDateTime.now();
    }
}
