package com.kafka.libraryeventproducer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.libraryeventproducer.domain.Book;
import com.kafka.libraryeventproducer.domain.LibraryEvent;
import com.kafka.libraryeventproducer.domain.enums.LibraryEventType;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"library-events","put-library-events"}, partitions = 3)
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"})
class LibraryEventControllerIntegrationTest {

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    ObjectMapper objectMapper;

    private Consumer<Integer, String> consumer;

    @BeforeEach
    void setUp() {
        HashMap<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("group1", "true", embeddedKafkaBroker));
        consumer = new DefaultKafkaConsumerFactory<>(configs, new IntegerDeserializer(), new StringDeserializer()).createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer,"library-events");
    }

    @AfterEach
    void tearDown() {
        consumer.close();
    }

    @Test
    @Timeout(5)
    void postLibraryEvent_validInput_validValueAndStatusCode() throws JsonProcessingException, InterruptedException {
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

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LibraryEvent> request = new HttpEntity<>(libraryEvent, headers);

        ResponseEntity<LibraryEvent> exchange = testRestTemplate.exchange("/libraryevent", HttpMethod.POST, request, LibraryEvent.class);
        Assertions.assertEquals(HttpStatus.CREATED, exchange.getStatusCode());

        ConsumerRecord<Integer, String> singleRecord = KafkaTestUtils.getSingleRecord(consumer, "library-events");
        String value = singleRecord.value();
        Assertions.assertEquals(libraryEventAsJson,value);
    }

    @Test
    @Timeout(5)
    void putLibraryEvent_validInput_validValueAndStatusCode() throws JsonProcessingException, InterruptedException {
        Book book = Book.builder()
                .bookId(120)
                .bookName("Harry")
                .author("Potter")
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .eventId(10)
                .type(LibraryEventType.UPDATE)
                .book(book)
                .build();

        String libraryEventAsJson = objectMapper.writeValueAsString(libraryEvent);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LibraryEvent> request = new HttpEntity<>(libraryEvent, headers);

        ResponseEntity<LibraryEvent> exchange = testRestTemplate.exchange("/libraryevent", HttpMethod.PUT, request, LibraryEvent.class);
        Assertions.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        ConsumerRecords<Integer, String> records = KafkaTestUtils.getRecords(consumer);
        for(ConsumerRecord<Integer, String> record : records) {
            System.out.println(record.value());
        }
        ConsumerRecord<Integer, String> record = records.iterator().next();
        String value = record.value();
        Assertions.assertEquals(libraryEventAsJson,value);
    }

    @Test
    @Timeout(5)
    void putLibraryEvent_invalidInput_nullEventId_badRequestStatusCode() {
        Book book = Book.builder()
                .bookId(120)
                .bookName("Harry")
                .author("Potter")
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .eventId(null)
                .type(LibraryEventType.UPDATE)
                .book(book)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LibraryEvent> request = new HttpEntity<>(libraryEvent, headers);

        ResponseEntity<LibraryEvent> exchange = testRestTemplate.exchange("/libraryevent", HttpMethod.PUT, request, LibraryEvent.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
    }
}
