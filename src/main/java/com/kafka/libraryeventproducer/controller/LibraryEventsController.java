package com.kafka.libraryeventproducer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kafka.libraryeventproducer.domain.Book;
import com.kafka.libraryeventproducer.domain.LibraryEvent;
import com.kafka.libraryeventproducer.domain.enums.LibraryEventType;
import com.kafka.libraryeventproducer.domain.dto.BookDTO;
import com.kafka.libraryeventproducer.operations.LibraryOperation;
import com.kafka.libraryeventproducer.producer.LibraryEventProducer;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/libraryevent")
public class LibraryEventsController implements LibraryOperation {

    LibraryEventProducer libraryEventProducer;

    @Override
    public ResponseEntity<LibraryEvent> addBook(@RequestBody @Valid LibraryEvent libraryEvent) throws JsonProcessingException {
        libraryEvent.setType(LibraryEventType.NEW);
        //Depends on client requirements -> Return something after messaging - Synchronous, otherwise - Asynchronous
        libraryEventProducer.sendLibraryEvent(libraryEvent);
//        SendResult<Integer, String> sendResult = libraryEventProducer.sendLibraryEventSynchronous(libraryEvent);
//        libraryEventProducer.sendLibraryEventApproach3(libraryEvent);

        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

    @Override
    public BookDTO updateBook(Book book) {
        return null;
    }
}
