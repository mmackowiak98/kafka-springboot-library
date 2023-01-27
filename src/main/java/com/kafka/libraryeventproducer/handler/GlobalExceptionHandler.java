package com.kafka.libraryeventproducer.handler;

import com.kafka.libraryeventproducer.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(GlobalException.class)
    public ProblemDetail handleException(GlobalException ex) {
        return ProblemDetail.forStatusAndDetail(ex.getCode(),ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        String collectedInfo = fieldErrors.stream()
                .map(f -> f.getField() + " - " + f.getDefaultMessage())
                .sorted()
                .collect(Collectors.joining(","));
        return ProblemDetail.forStatusAndDetail(ex.getStatusCode(),collectedInfo);
    }
}
