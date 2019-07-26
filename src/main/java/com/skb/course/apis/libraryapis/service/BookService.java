package com.skb.course.apis.libraryapis.service;

import com.skb.course.apis.libraryapis.entity.AuthorEntity;
import com.skb.course.apis.libraryapis.entity.BookEntity;
import com.skb.course.apis.libraryapis.entity.BookStatusEntity;
import com.skb.course.apis.libraryapis.entity.PublisherEntity;
import com.skb.course.apis.libraryapis.exception.LibraryResourceNotFoundException;
import com.skb.course.apis.libraryapis.model.Author;
import com.skb.course.apis.libraryapis.model.Book;
import com.skb.course.apis.libraryapis.model.BookStatus;
import com.skb.course.apis.libraryapis.repository.AuthorRepository;
import com.skb.course.apis.libraryapis.repository.BookRepository;
import com.skb.course.apis.libraryapis.repository.BookStatusRepository;
import com.skb.course.apis.libraryapis.repository.PublisherRepository;
import com.skb.course.apis.libraryapis.util.LibraryApiUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public Book addBook(Book bookToBeAdded, String traceId) throws LibraryResourceNotFoundException {
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
            throw new LibraryResourceNotFoundException(traceId, "Publisher mentioned for the book does not exist");
        }

        // Save book to DB
        BookEntity addedBook = bookRepository.save(bookEntity);

        // Manage 1-1 relationship
        // 1-1 relationship: Create the (Child) BookStatusEntity object
        BookStatusEntity bookStatusEntity = new BookStatusEntity(addedBook.getBookId(), bookToBeAdded.getBookStatus().getState(),
                bookToBeAdded.getBookStatus().getTotalNumberOfCopies(), 0);

        // 1-1 relationship: Set parent reference(BookEntity) in child entity(BookStatusEntity)
        bookStatusEntity.setBookEntity(bookEntity);

        // Save the child entity (BookStatusEntity)
        bookStatusRepository.save(bookStatusEntity);

        bookToBeAdded.setBookId(addedBook.getBookId());
        bookToBeAdded.setBookStatus(createBookStatusFromEntity(bookStatusEntity));
        return bookToBeAdded;
    }

    public Book getBook(int bookId, String traceId) throws LibraryResourceNotFoundException {
        Optional<BookEntity> bookEntity = bookRepository.findById(bookId);
        Book book = null;
        if(bookEntity.isPresent()) {
            BookEntity ue = bookEntity.get();
            book = createBookFromEntity(ue);
        } else {
            throw new LibraryResourceNotFoundException(traceId, "Book Id: " + bookId + " Not Found");
        }
        return book;
    }

    public Book updateBook(Book bookToBeUpdated, String traceId) throws LibraryResourceNotFoundException {
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
                    throw new LibraryResourceNotFoundException(traceId, "Publisher mentioned for the book does not exist");
                }
            }
            bookRepository.save(be);
            book = createBookFromEntity(be);
        } else {
            throw new LibraryResourceNotFoundException(traceId, "Book Id: " + bookToBeUpdated.getBookId() + " Not Found");
        }
        return book;
    }

    public void deleteAuthor(int bookId, String traceId) throws LibraryResourceNotFoundException {

        try {
            bookRepository.deleteById(bookId);
        } catch (
            EmptyResultDataAccessException e) {
            throw new LibraryResourceNotFoundException(traceId, "Book Id: " + bookId + " Not Found");
        }
    }

    public Book addBookAuhors(int bookId, Set<Integer> authorIds, String traceId) throws LibraryResourceNotFoundException {
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

            if(authors.size() == 0) {
                String authorsList = authorIds.stream()
                                            .map(authorId -> authorId.toString().concat(" "))
                                            .reduce("", String::concat);

                throw new LibraryResourceNotFoundException(traceId, "Book Id: " + bookId + ". None of the authors - " + authorsList + " found.");
            }
            be.setAuthors(authors);
            bookRepository.save(be);
            book = createBookFromEntity(be);
        } else {
            throw new LibraryResourceNotFoundException(traceId, "Book Id: " + bookId + " Not Found");
        }
        return book;
    }

    public Book searchBookByIsbn(String isbn, String traceId) throws LibraryResourceNotFoundException {

        BookEntity book = bookRepository.findByIsbn(isbn);
        if(book == null) {
            throw new LibraryResourceNotFoundException(traceId, "No book found for ISBN: " + isbn);
        }
        return createBookFromEntity(book);

    }

    public List<Book> searchBookByTitle(String title, String traceId) throws LibraryResourceNotFoundException {
        List<BookEntity> books = bookRepository.findByTitleContaining(title);
        if(books == null || books.size() ==0) {
            throw new LibraryResourceNotFoundException(traceId, "No book found matching/having: " + title);
        }

        return books.stream()
                .map(book -> createBookFromEntity(book))
                .collect(Collectors.toList());
    }

    public Book createBookFromEntity(BookEntity be) {
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
        return new BookStatus(bse.getState(), bse.getTotalNumberOfCopies(), bse.getNumberOfCopiesIssued());
    }

    private Author createAuthorFromAuthorEntity(AuthorEntity ae) {
        return new Author(ae.getAuthorId(), ae.getFirstName(), ae.getLastName());
    }
}
