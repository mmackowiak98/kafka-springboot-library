package com.kafka.libraryeventproducer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.libraryeventproducer.domain.Book;
import com.kafka.libraryeventproducer.domain.LibraryEvent;
import com.kafka.libraryeventproducer.domain.enums.LibraryEventType;
import com.kafka.libraryeventproducer.producer.LibraryEventProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LibraryEventsController.class)
@AutoConfigureMockMvc
public class LibraryEventControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    LibraryEventProducer libraryEventProducer;


    @Test
    void postLibraryEvent_validInput_createdStatusCode() throws Exception {
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

        String libraryEventAsJson = objectMapper.writeValueAsString(libraryEvent);

        when(libraryEventProducer.sendLibraryEvent(isA(LibraryEvent.class))).thenReturn(null);


        mockMvc.perform(post("/libraryevent")
                .content(libraryEventAsJson)
                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isCreated());
    }

    @Test
    void postLibraryEvent_invalidInput_nullBook_4xxStatusCode() throws Exception {

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .eventId(null)
                .type(LibraryEventType.NEW)
                .book(null)
                .build();

        String libraryEventAsJson = objectMapper.writeValueAsString(libraryEvent);

        when(libraryEventProducer.sendLibraryEvent(isA(LibraryEvent.class))).thenReturn(null);

        mockMvc.perform(post("/libraryevent")
                .content(libraryEventAsJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void postLibraryEvent_invalidInput_emptyBook_4xxStatusCode() throws Exception {

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .eventId(null)
                .type(LibraryEventType.NEW)
                .book(new Book())
                .build();

        String libraryEventAsJson = objectMapper.writeValueAsString(libraryEvent);

        when(libraryEventProducer.sendLibraryEvent(isA(LibraryEvent.class))).thenReturn(null);

        mockMvc.perform(post("/libraryevent")
                        .content(libraryEventAsJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }
}
