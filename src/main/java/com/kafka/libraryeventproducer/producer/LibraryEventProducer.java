package com.kafka.libraryeventproducer.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.libraryeventproducer.domain.LibraryEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@Slf4j
@AllArgsConstructor
public class LibraryEventProducer {

    private KafkaTemplate<Integer, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    //Asynchronous way
    public CompletableFuture<SendResult<Integer, String>> sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {

        String value = objectMapper.writeValueAsString(libraryEvent);

        CompletableFuture<SendResult<Integer, String>> completableFuture =
                kafkaTemplate.sendDefault(libraryEvent.getEventId(), value);

        return completableFuture.whenComplete((result, ex) -> {
            if (ex != null) {
                handleFailure(ex);
            } else {
                try {
                    handleSuccess(libraryEvent.getEventId(), value, result);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    //Synchronous way
    public SendResult<Integer, String> sendLibraryEventSynchronous(LibraryEvent libraryEvent) throws JsonProcessingException {
        String value = objectMapper.writeValueAsString(libraryEvent);
        SendResult<Integer, String> sendResult = null;
        try {
            sendResult = kafkaTemplate.sendDefault(libraryEvent.getEventId(), value).get(1, TimeUnit.MINUTES);
        } catch (TimeoutException | ExecutionException | InterruptedException e) {
            log.error("Error in sendLibraryEventSynchronous method {}, cause {}", e.getMessage(), e.getCause());
            throw new RuntimeException(e);
        }
        return sendResult;
    }

    //Explicitly providing topic to send message to with use of ProducerRecord
    public CompletableFuture<SendResult<Integer, String>> sendLibraryEventApproach3(LibraryEvent libraryEvent) throws JsonProcessingException {

        String value = objectMapper.writeValueAsString(libraryEvent);

        ProducerRecord<Integer, String> producerRecord = buildProducerRecord(libraryEvent.getEventId(), value, "library-events");
        CompletableFuture<SendResult<Integer, String>> completableFuture =
                kafkaTemplate.send(producerRecord);

        return completableFuture.whenComplete((result, ex) -> {
            if (ex != null) {
                handleFailure(ex);
            } else {
                try {
                    handleSuccess(libraryEvent.getEventId(), value, result);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    private ProducerRecord<Integer, String> buildProducerRecord(Integer key, String value, String topic) {

        //META-DATA of Message
        List<RecordHeader> recordHeaders = List.of(new RecordHeader("event-source", "scanner".getBytes()));

        return new ProducerRecord(topic, null, key, value, recordHeaders);
    }

    private void handleSuccess(Integer key, String value, SendResult<Integer, String> result) throws JsonProcessingException {
        log.info("Message Sent Succesfully for key: {} and the value is: {}, and partition is: {}",
                key,
                value,
                result.getRecordMetadata().partition());
    }

    private void handleFailure(Throwable ex) {
        log.error("Error has occured {}, with message {}", ex.getCause(), ex.getMessage());
        try {
            throw ex;
        } catch (Throwable e) {
            log.error("Error in handleFailure method {}", e.getMessage());
        }
    }
}
