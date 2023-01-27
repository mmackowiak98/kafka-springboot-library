package com.kafka.libraryeventproducer.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.libraryeventproducer.domain.Book;
import com.kafka.libraryeventproducer.domain.LibraryEvent;
import com.kafka.libraryeventproducer.domain.enums.LibraryEventType;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LibraryEventProducerUnitTest {
    @Mock
    KafkaTemplate kafkaTemplate;
    @Spy
    ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    LibraryEventProducer libraryEventProducer;

    @Test
    void sendLibraryEvent_AsynchronousMethod_onFailure_throwsException() throws JsonProcessingException, ExecutionException, InterruptedException {

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

        CompletableFuture<Object> completableFuture = new CompletableFuture<>();
        completableFuture.completeExceptionally(new RuntimeException("Exception Calling Kafka"));

        when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(completableFuture);

        Assertions.assertThrows(Exception.class, () -> libraryEventProducer.sendLibraryEventApproach3(libraryEvent).get());
    }

    @Test
    void sendLibraryEvent_AsynchronousMethod_onSuccess_validOutput() throws JsonProcessingException, ExecutionException, InterruptedException {
        String topicName = "library-events";
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

        String libraryEventAsString = objectMapper.writeValueAsString(libraryEvent);

        CompletableFuture<Object> completableFuture = new CompletableFuture<>();

        ProducerRecord<Integer, String> producerRecord = new ProducerRecord<>(
                topicName,
                libraryEvent.getEventId(),
                libraryEventAsString);

        RecordMetadata recordMetadata = new RecordMetadata(
                new TopicPartition(topicName, 1),
                1L,
                1,
                System.currentTimeMillis(),
                1,
                2);

        SendResult<Integer, String> expectedSendResult = new SendResult<>(producerRecord, recordMetadata);

        completableFuture.complete(expectedSendResult);

        when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(completableFuture);

        CompletableFuture<SendResult<Integer, String>> resultCompletableFuture = libraryEventProducer.sendLibraryEventApproach3(libraryEvent);
        SendResult<Integer, String> actualSendResult = resultCompletableFuture.get();

        Assertions.assertEquals(expectedSendResult,actualSendResult);
        Assertions.assertEquals(expectedSendResult.getRecordMetadata().partition(),actualSendResult.getRecordMetadata().partition());
    }
}
