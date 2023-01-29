package com.kafka.libraryeventconsumer.repository;

import com.kafka.libraryeventconsumer.entity.LibraryEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibraryEventRepository extends JpaRepository<LibraryEvent, Integer> {
}
