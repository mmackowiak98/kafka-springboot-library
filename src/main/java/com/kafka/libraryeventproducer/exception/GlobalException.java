package com.kafka.libraryeventproducer.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
public class GlobalException extends RuntimeException {
    private String message;
    private HttpStatusCode code;
}
