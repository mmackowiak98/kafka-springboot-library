package com.kafka.libraryeventconsumer.entity;

import com.kafka.libraryeventconsumer.entity.enums.LibraryEventType;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class LibraryEvent {
    @Id
    @GeneratedValue
    private Integer eventId;
    @Enumerated(EnumType.STRING)
    private LibraryEventType type;
    @OneToOne(mappedBy = "libraryEvent", cascade = CascadeType.ALL)
    @ToString.Exclude
    private Book book;
}
