package com.kafka.libraryeventconsumer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.libraryeventconsumer.entity.LibraryEvent;
import com.kafka.libraryeventconsumer.entity.enums.LibraryEventType;
import com.kafka.libraryeventconsumer.repository.LibraryEventRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class LibraryEventService {
    private LibraryEventRepository libraryEventRepository;

    private final ObjectMapper objectMapper;
    private EntityManager entityManager;


    public void processLibraryEvent(ConsumerRecord<Integer, String> record) {

        try {
            LibraryEvent libraryEvent = objectMapper.readValue(record.value(), LibraryEvent.class);
            log.info("Library Event : {}", libraryEvent);

            if (libraryEvent.getType() == LibraryEventType.NEW) {
                save(libraryEvent);
            } else if (libraryEvent.getType() == LibraryEventType.UPDATE) {
                update(libraryEvent);
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    private void update(LibraryEvent libraryEvent) {

    }


    private void save(LibraryEvent libraryEvent) {
        libraryEvent.getBook().setLibraryEvent(libraryEvent);
        LibraryEvent mergedLibraryEvent = entityManager.merge(libraryEvent);
        libraryEventRepository.save(mergedLibraryEvent);
        log.info("Successfully persisted {}", libraryEvent);
    }


}
