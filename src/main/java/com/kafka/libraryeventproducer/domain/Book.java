package com.kafka.libraryeventproducer.domain;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    private int bookId;
    private String bookName;
    private String author;
}
