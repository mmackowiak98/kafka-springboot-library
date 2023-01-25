package com.kafka.libraryeventproducer.operations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kafka.libraryeventproducer.domain.Book;
import com.kafka.libraryeventproducer.domain.LibraryEvent;
import com.kafka.libraryeventproducer.domain.dto.BookDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/default")
public interface LibraryOperation {

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<LibraryEvent> addBook(LibraryEvent libraryEvent) throws JsonProcessingException;

    @PutMapping()
    @ResponseStatus(HttpStatus.OK)
    BookDTO updateBook(Book book);


}
