package com.kafka.libraryeventproducer.operations;

import com.kafka.libraryeventproducer.domain.Book;
import com.kafka.libraryeventproducer.domain.dto.BookDTO;

import java.util.function.Function;

public class BookMapper implements Function<Book, BookDTO> {
    @Override
    public BookDTO apply(Book book) {
        return new BookDTO(
                book.getBookName(),
                book.getAuthor()
        );
    }
}
