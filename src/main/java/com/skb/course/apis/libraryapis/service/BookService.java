package com.skb.course.apis.libraryapis.service;

import com.skb.course.apis.libraryapis.entity.BookEntity;
import com.skb.course.apis.libraryapis.entity.UserEntity;
import com.skb.course.apis.libraryapis.model.Book;
import com.skb.course.apis.libraryapis.model.User;
import com.skb.course.apis.libraryapis.repository.BookRepository;
import com.skb.course.apis.libraryapis.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public Book addBook(Book bookToBeAdded) {
        BookEntity bookEntity = new BookEntity(
                bookToBeAdded.getIsbn(),
                bookToBeAdded.getTitle(),
                bookToBeAdded.getPublisherId(),
                bookToBeAdded.getYearPublished(),
                bookToBeAdded.getEdition());

        BookEntity addedBook = bookRepository.save(bookEntity);

        bookToBeAdded.setBookId(addedBook.getBookId());
        return bookToBeAdded;
    }
}
