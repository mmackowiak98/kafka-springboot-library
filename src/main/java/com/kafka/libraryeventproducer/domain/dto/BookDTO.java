package com.kafka.libraryeventproducer.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BookDTO {
    private String bookName;
    private String bookAuthor;
}
