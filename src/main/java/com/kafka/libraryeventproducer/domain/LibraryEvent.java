package com.kafka.libraryeventproducer.domain;

import com.kafka.libraryeventproducer.domain.enums.LibraryEventType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    @Valid
    private Book book;
}
