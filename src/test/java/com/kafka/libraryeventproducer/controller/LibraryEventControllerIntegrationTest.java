package com.kafka.libraryeventproducer.controller;

import com.kafka.libraryeventproducer.domain.Book;
import com.kafka.libraryeventproducer.domain.LibraryEvent;
import com.kafka.libraryeventproducer.domain.LibraryEventType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LibraryEventControllerIntegrationTest {

    @Autowired
    TestRestTemplate testRestTemplate;

    @Test
    void postLibraryEvent_validInput_validOutputAndStatusCode() {
        Book book = Book.builder()
                .bookId(1)
                .bookName("Harry")
                .author("Potter")
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .eventId(null)
                .type(LibraryEventType.NEW)
                .book(book)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LibraryEvent> request = new HttpEntity<>(libraryEvent, headers);

        ResponseEntity<LibraryEvent> exchange = testRestTemplate.exchange("/libraryevent", HttpMethod.POST, request, LibraryEvent.class);
        Assertions.assertEquals(HttpStatus.CREATED,exchange.getStatusCode());
    }
}
