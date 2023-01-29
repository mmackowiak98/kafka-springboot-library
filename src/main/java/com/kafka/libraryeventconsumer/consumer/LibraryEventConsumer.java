package com.kafka.libraryeventconsumer.consumer;

import com.kafka.libraryeventconsumer.service.LibraryEventService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@AllArgsConstructor
public class LibraryEventConsumer {

    private LibraryEventService libraryEventService;

    @KafkaListener(topics = {"library-events"})
    public void onMessage(ConsumerRecord<Integer, String> record) {
        log.info("Consumer Record : {}", record);
        libraryEventService.processLibraryEvent(record);
    }
}
