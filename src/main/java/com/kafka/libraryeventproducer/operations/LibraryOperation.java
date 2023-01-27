package com.kafka.libraryeventproducer.operations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kafka.libraryeventproducer.domain.LibraryEvent;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/default")
public interface LibraryOperation {

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<LibraryEvent> postLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent) throws JsonProcessingException;

    @PutMapping()
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<LibraryEvent> updateLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent) throws JsonProcessingException;


}
