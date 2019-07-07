package com.skb.course.apis.libraryapis.service;

import com.skb.course.apis.libraryapis.entity.BookEntity;
import com.skb.course.apis.libraryapis.entity.BookStatusEntity;
import com.skb.course.apis.libraryapis.model.Book;
import com.skb.course.apis.libraryapis.repository.BookRepository;
import com.skb.course.apis.libraryapis.repository.BookStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookStatusRepository bookStatusRepository;

    public Book addBook(Book bookToBeAdded) {
        BookEntity bookEntity = new BookEntity(
                bookToBeAdded.getIsbn(),
                bookToBeAdded.getTitle(),
                bookToBeAdded.getPublisherId(),
                bookToBeAdded.getYearPublished(),
                bookToBeAdded.getEdition());

        BookEntity addedBook = bookRepository.save(bookEntity);

        BookStatusEntity bookStatusEntity = new BookStatusEntity(addedBook.getBookId(), bookToBeAdded.getBookStatus().getState(),
                bookToBeAdded.getBookStatus().getNumberOfCopiesAvailable(), 0);
        bookStatusRepository.save(bookStatusEntity);

        bookToBeAdded.setBookId(addedBook.getBookId());


        return bookToBeAdded;
    }
}
