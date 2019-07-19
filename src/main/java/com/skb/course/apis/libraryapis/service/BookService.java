package com.skb.course.apis.libraryapis.service;

import com.skb.course.apis.libraryapis.entity.AuthorEntity;
import com.skb.course.apis.libraryapis.entity.BookEntity;
import com.skb.course.apis.libraryapis.entity.BookStatusEntity;
import com.skb.course.apis.libraryapis.entity.PublisherEntity;
import com.skb.course.apis.libraryapis.exception.BookNotFoundException;
import com.skb.course.apis.libraryapis.exception.PublisherNotFoundException;
import com.skb.course.apis.libraryapis.model.Author;
import com.skb.course.apis.libraryapis.model.Book;
import com.skb.course.apis.libraryapis.model.BookStatus;
import com.skb.course.apis.libraryapis.repository.AuthorRepository;
import com.skb.course.apis.libraryapis.repository.BookRepository;
import com.skb.course.apis.libraryapis.repository.BookStatusRepository;
import com.skb.course.apis.libraryapis.repository.PublisherRepository;
import com.skb.course.apis.libraryapis.util.LibraryApiUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookService {

    private BookRepository bookRepository;
    private BookStatusRepository bookStatusRepository;
    private AuthorRepository authorRepository;
    private PublisherRepository publisherRepository;

    public BookService(BookRepository bookRepository, BookStatusRepository bookStatusRepository, AuthorRepository authorRepository, PublisherRepository publisherRepository) {
        this.bookRepository = bookRepository;
        this.bookStatusRepository = bookStatusRepository;
        this.authorRepository = authorRepository;
        this.publisherRepository = publisherRepository;
    }

    public Book addBook(Book bookToBeAdded) throws PublisherNotFoundException {
        BookEntity bookEntity = new BookEntity(
                bookToBeAdded.getIsbn(),
                bookToBeAdded.getTitle(),
                bookToBeAdded.getYearPublished(),
                bookToBeAdded.getEdition());

        // Get the parent of Book (Publisher is parent. Book has 1-M relationship with Publisher)
        Optional<PublisherEntity> publisherEntity = publisherRepository.findById(bookToBeAdded.getPublisherId());
        if(publisherEntity.isPresent()) {
            bookEntity.setPublisher(publisherEntity.get());
        } else {
            throw new PublisherNotFoundException("Publisher mentioned for the book does not exist");
        }

        // Save book to DB
        BookEntity addedBook = bookRepository.save(bookEntity);

        // Manage 1-1 relationship
        // 1-1 relationship: Create the (Child) BookStatusEntity object
        BookStatusEntity bookStatusEntity = new BookStatusEntity(addedBook.getBookId(), bookToBeAdded.getBookStatus().getState(),
                bookToBeAdded.getBookStatus().getNumberOfCopiesAvailable(), 0);

        // 1-1 relationship: Set parent reference(BookEntity) in child entity(BookStatusEntity)
        bookStatusEntity.setBookEntity(bookEntity);

        // Save the child entity (BookStatusEntity)
        bookStatusRepository.save(bookStatusEntity);

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

    public Book updateBook(Book bookToBeUpdated) throws BookNotFoundException, PublisherNotFoundException {
        Optional<BookEntity> bookEntity = bookRepository.findById(bookToBeUpdated.getBookId());
        Book book = null;
        if(bookEntity.isPresent()) {
            BookEntity be = bookEntity.get();
            if(LibraryApiUtils.doesStringValueExist(bookToBeUpdated.getEdition())) {
                be.setEdition(bookToBeUpdated.getEdition());
            }
            if(bookToBeUpdated.getYearPublished() != null) {
                be.setYearPublished(bookToBeUpdated.getYearPublished());
            }
            Optional<PublisherEntity> publisherEntity = publisherRepository.findById(bookToBeUpdated.getPublisherId());
            if(bookToBeUpdated.getPublisherId() != null) {
                if(publisherEntity.isPresent()) {
                    be.setPublisher(publisherEntity.get());
                } else {
                    throw new PublisherNotFoundException("Publisher mentioned for the book does not exist");
                }
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
        Book book = new Book(be.getBookId(), be.getIsbn(), be.getTitle(), be.getPublisher().getPublisherId() , be.getYearPublished(), be.getEdition()
                ,createBookStatusFromEntity(bookStatusRepository.findById(be.getBookId()).get())
        );

        if(be.getAuthors() != null && be.getAuthors().size() > 0) {
            Set<Author> authors = be.getAuthors().stream()
                    .map(authorEntity -> {
                        AuthorEntity ae = authorRepository.findById(authorEntity.getAuthorId()).get();
                        return createAuthorFromAuthorEntity(ae);
                    })
                    .collect(Collectors.toSet());
            book.setAuthors(authors);
        }
        return book;
    }

    private BookStatus createBookStatusFromEntity(BookStatusEntity bse) {
        return new BookStatus(bse.getState(), bse.getNumberOfCopiesAvailable(), bse.getNumberOfCopiesIssued());
    }

    private Author createAuthorFromAuthorEntity(AuthorEntity ae) {
        return new Author(ae.getAuthorId(), ae.getFirstName(), ae.getLastName());
    }
}
