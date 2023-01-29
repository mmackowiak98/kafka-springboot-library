package com.kafka.libraryeventproducer.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kafka.libraryeventproducer.domain.LibraryEvent;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

public interface LibraryEventProducerOperation {

    CompletableFuture<SendResult<Integer, String>> sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException;
    SendResult<Integer, String> sendLibraryEventSynchronous(LibraryEvent libraryEvent) throws JsonProcessingException;
    CompletableFuture<SendResult<Integer, String>> sendLibraryEventApproach3(LibraryEvent libraryEvent) throws JsonProcessingException;
}
