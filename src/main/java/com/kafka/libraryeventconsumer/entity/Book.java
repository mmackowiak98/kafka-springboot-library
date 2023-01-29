package com.kafka.libraryeventconsumer.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Book {
    @Id
    @GeneratedValue
    private Integer bookId;
    private String bookName;
    private String author;
    @OneToOne
    @JoinColumn(name = "eventId")
    private LibraryEvent libraryEvent;
}
