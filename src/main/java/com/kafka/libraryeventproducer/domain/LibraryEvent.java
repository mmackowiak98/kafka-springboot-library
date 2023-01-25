package com.kafka.libraryeventproducer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LibraryEvent {
    private Integer eventId;
    private LibraryEventType type;
    private Book book;
}
