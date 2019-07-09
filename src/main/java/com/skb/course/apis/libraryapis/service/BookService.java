package com.skb.course.apis.libraryapis.service;

import com.skb.course.apis.libraryapis.entity.AuthorEntity;
import com.skb.course.apis.libraryapis.entity.BookEntity;
import com.skb.course.apis.libraryapis.entity.BookStatusEntity;
import com.skb.course.apis.libraryapis.exception.BookNotFoundException;
import com.skb.course.apis.libraryapis.model.Book;
import com.skb.course.apis.libraryapis.model.BookStatus;
import com.skb.course.apis.libraryapis.repository.AuthorRepository;
import com.skb.course.apis.libraryapis.repository.BookRepository;
import com.skb.course.apis.libraryapis.repository.BookStatusRepository;
import com.skb.course.apis.libraryapis.util.Utility;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookService {

    private BookRepository bookRepository;
    private BookStatusRepository bookStatusRepository;
    private AuthorRepository authorRepository;

    public BookService(BookRepository bookRepository, BookStatusRepository bookStatusRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.bookStatusRepository = bookStatusRepository;
        this.authorRepository = authorRepository;
    }

    public Book addBook(Book bookToBeAdded) {
        BookEntity bookEntity = new BookEntity(
                bookToBeAdded.getIsbn(),
                bookToBeAdded.getTitle(),
                bookToBeAdded.getPublisherId(),
                bookToBeAdded.getYearPublished(),
                bookToBeAdded.getEdition());

        BookStatusEntity bookStatusEntity = new BookStatusEntity(bookToBeAdded.getBookStatus().getState(),
                bookToBeAdded.getBookStatus().getNumberOfCopiesAvailable(), 0);


        // Set child reference(userProfile) in parent entity(user)
        bookEntity.setBookStatus(bookStatusEntity);

        // Set parent reference(user) in child entity(userProfile)
        bookStatusEntity.setBookEntity(bookEntity);

        BookEntity addedBook = bookRepository.save(bookEntity);

        bookToBeAdded.setBookId(addedBook.getBookId());
        bookToBeAdded.setBookStatus(createBookStatusFromEntity(bookStatusEntity));
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

    public Book addBookAuhors(int bookId, Set<Integer> authorIds) throws BookNotFoundException {
        if(authorIds == null || authorIds.size() == 0) {
            throw new IllegalArgumentException("Invalid Authors list");
        }
        Optional<BookEntity> bookEntity = bookRepository.findById(bookId);
        Book book = null;
        if(bookEntity.isPresent()) {
            BookEntity be = bookEntity.get();
            Set<AuthorEntity> authors = authorIds.stream()
                    .map(authorId ->
                            authorRepository.findById(authorId)
                    ).collect(Collectors.toSet()).stream()
                    .filter(ae -> ae.isPresent() == true)
                    .map(ae -> ae.get())
                    .collect(Collectors.toSet());
            be.setAuthors(authors);
            bookRepository.save(be);
            book = createBookFromEntity(be);
        } else {
            throw new BookNotFoundException("Book Id: " + bookId + " Not Found");
        }
        return book;
    }

    private Book createBookFromEntity(BookEntity be) {
        return new Book(be.getBookId(), be.getIsbn(), be.getTitle(), be.getPublisherId(), be.getYearPublished(), be.getEdition()
                ,createBookStatusFromEntity(bookStatusRepository.findById(be.getBookId()).get())
        );
    }

    private BookStatus createBookStatusFromEntity(BookStatusEntity bse) {
        return new BookStatus(bse.getState(), bse.getNumberOfCopiesAvailable(), bse.getNumberOfCopiesIssued());
    }
}
