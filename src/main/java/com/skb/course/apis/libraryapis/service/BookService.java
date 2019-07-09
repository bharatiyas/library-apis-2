package com.skb.course.apis.libraryapis.service;

import com.skb.course.apis.libraryapis.entity.AuthorEntity;
import com.skb.course.apis.libraryapis.entity.BookEntity;
import com.skb.course.apis.libraryapis.entity.BookStatusEntity;
import com.skb.course.apis.libraryapis.exception.BookNotFoundException;
import com.skb.course.apis.libraryapis.model.Author;
import com.skb.course.apis.libraryapis.model.Book;
import com.skb.course.apis.libraryapis.model.BookStatus;
import com.skb.course.apis.libraryapis.repository.BookRepository;
import com.skb.course.apis.libraryapis.repository.BookStatusRepository;
import com.skb.course.apis.libraryapis.util.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

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

    public Book getBook(int bookId) throws BookNotFoundException {
        Optional<BookEntity> bookEntity = bookRepository.findById(bookId);
        Book book = null;
        if(bookEntity.isPresent()) {
            BookEntity ue = bookEntity.get();
            book = createBookFromEntity(ue);
        } else {
            throw new BookNotFoundException("Book Id: " + bookId + " Not Found");
        }
        return book;
    }

    public Book updateBook(Book bookToBeUpdated) throws BookNotFoundException {
        Optional<BookEntity> bookEntity = bookRepository.findById(bookToBeUpdated.getBookId());
        Book book = null;
        if(bookEntity.isPresent()) {
            BookEntity be = bookEntity.get();
            if(Utility.doesStringValueExist(bookToBeUpdated.getEdition())) {
                be.setEdition(bookToBeUpdated.getEdition());
            }
            if(bookToBeUpdated.getYearPublished() != null) {
                be.setYearPublished(bookToBeUpdated.getYearPublished());
            }
            if(bookToBeUpdated.getPublisherId() != null) {
                be.setPublisherId(bookToBeUpdated.getPublisherId());
            }
            bookRepository.save(be);
            book = createBookFromEntity(be);
        } else {
            throw new BookNotFoundException("Book Id: " + bookToBeUpdated.getBookId() + " Not Found");
        }
        return book;
    }

    public Book addBookAuhors(int bookId, Set<Author> authors) {
        if(authors == null || authors.size() == 0) {
            throw new IllegalArgumentException("Invalid Authors list");
        }
        Optional<BookEntity> bookEntity = bookRepository.findById(bookId);
        Book book = null;
        if(bookEntity.isPresent()) {
            BookEntity be = bookEntity.get();
            authors.stream()
                    .map(author -> {
                        return new AuthorEntity()
                    })
            be.setAuthors();
            bookRepository.save(be);
            book = createBookFromEntity(be);
        } else {
            throw new BookNotFoundException("Book Id: " + bookToBeUpdated.getBookId() + " Not Found");
        }
        return book;
    }

    private Book createBookFromEntity(BookEntity be) {
        // int bookId, BookStatusState state, int numberOfCopiesAvailable, int numberOfCopiesIssued
        BookStatusEntity bse = be.getBookStatus();
        BookStatus bs = new BookStatus(bse.getState(), bse.getNumberOfCopiesAvailable(), bse.getNumberOfCopiesIssued());
        return new Book(be.getBookId(), be.getIsbn(), be.getTitle(), be.getPublisherId(), be.getYearPublished(), be.getEdition(), bs);
    }
}
