package com.skb.course.apis.libraryapis.service;

import com.skb.course.apis.libraryapis.LibraryApiTestUtil;
import com.skb.course.apis.libraryapis.TestConstants;
import com.skb.course.apis.libraryapis.entity.BookEntity;
import com.skb.course.apis.libraryapis.entity.BookStatusEntity;
import com.skb.course.apis.libraryapis.exception.LibraryResourceAlreadyExistException;
import com.skb.course.apis.libraryapis.exception.LibraryResourceNotFoundException;
import com.skb.course.apis.libraryapis.model.Book;
import com.skb.course.apis.libraryapis.model.BookStatus;
import com.skb.course.apis.libraryapis.model.BookStatusState;
import com.skb.course.apis.libraryapis.model.Gender;
import com.skb.course.apis.libraryapis.repository.AuthorRepository;
import com.skb.course.apis.libraryapis.repository.BookRepository;
import com.skb.course.apis.libraryapis.repository.BookStatusRepository;
import com.skb.course.apis.libraryapis.repository.PublisherRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BookServiceTest {

    @Mock
    BookRepository bookRepository;

    @Mock
    private BookStatusRepository bookStatusRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private PublisherRepository publisherRepository;

    BookService bookService;

    @Before
    public void setUp() {
        bookService = new BookService(bookRepository, bookStatusRepository, authorRepository, publisherRepository);
    }

    @Test
    public void addBook_success() throws LibraryResourceNotFoundException {
        when(bookRepository.save(any(BookEntity.class))).thenReturn(createBookEntity());
        Book book = bookService.addBook(LibraryApiTestUtil.createBook(1), TestConstants.API_TRACE_ID);
        assertNotNull(book);
        assertNotNull(book.getBookId());
        assertEquals(TestConstants.TEST_BOOK_ISBN, book.getIsbn());
        assertEquals(TestConstants.TEST_BOOK_TITLE, book.getTitle());
    }

    @Test
    public void getBook_success() throws Exception {

        when(bookRepository.findById(anyInt())).thenReturn(createBookEntityOptional());
        Book book = bookService.getBook(123, TestConstants.API_TRACE_ID);

        assertNotNull(book);
        assertNotNull(book.getBookId());
        assertEquals(TestConstants.TEST_BOOK_ISBN, book.getIsbn());
        assertEquals(TestConstants.TEST_BOOK_TITLE, book.getTitle());

    }

    @Test(expected = LibraryResourceNotFoundException.class)
    public void getBook_failure_book_not_found() throws Exception {

        when(bookRepository.findById(anyInt())).thenReturn(Optional.empty());
        bookService.getBook(123, TestConstants.API_TRACE_ID);

    }

    @Test
    public void updateBook_success() throws LibraryResourceNotFoundException {

        BookEntity bookEntity = createBookEntity();
        when(bookRepository.save(any(BookEntity.class))).thenReturn(bookEntity);

        Book book = bookService.addBook(LibraryApiTestUtil.createBook(1), TestConstants.API_TRACE_ID);
        assertNotNull(book);
        assertNotNull(book.getBookId());

        int updatedYearPublished = book.getYearPublished() + 1;
        book.setYearPublished(updatedYearPublished);

        when(bookRepository.findById(anyInt())).thenReturn(createBookEntityOptional());

        book = bookService.updateBook(book, TestConstants.API_TRACE_ID);
        assertEquals(TestConstants.TEST_BOOK_ISBN, book.getIsbn());
        assertEquals(updatedYearPublished, book.getYearPublished().intValue());

    }

    @Test(expected = LibraryResourceNotFoundException.class)
    public void updateBook_failure_book_not_found() throws LibraryResourceNotFoundException {

        BookEntity bookEntity = createBookEntity();
        when(bookRepository.save(any(BookEntity.class))).thenReturn(bookEntity);

        Book book = bookService.addBook(LibraryApiTestUtil.createBook(1), TestConstants.API_TRACE_ID);
        assertNotNull(book);
        assertNotNull(book.getBookId());

        book.setYearPublished(book.getYearPublished() + 1);

        when(bookRepository.findById(anyInt())).thenReturn(Optional.empty());
        bookService.updateBook(book, TestConstants.API_TRACE_ID);

    }

    @Test
    public void deleteBook_success() throws LibraryResourceNotFoundException {

        when(bookRepository.save(any(BookEntity.class))).thenReturn(createBookEntity());
        Book book = bookService.addBook(LibraryApiTestUtil.createBook(1), TestConstants.API_TRACE_ID);
        assertNotNull(book);

        doNothing().when(bookRepository).deleteById(anyInt());
        bookService.deleteBook(book.getBookId(), TestConstants.API_TRACE_ID);
    }

    @Test(expected = LibraryResourceNotFoundException.class)
    public void deleteBook_failure_book_not_found() throws LibraryResourceNotFoundException {

        when(bookRepository.save(any(BookEntity.class))).thenReturn(createBookEntity());
        Book book = bookService.addBook(LibraryApiTestUtil.createBook(1), TestConstants.API_TRACE_ID);
        assertNotNull(book);

        doThrow(EmptyResultDataAccessException.class).when(bookRepository).deleteById(anyInt());
        bookService.deleteBook(book.getBookId(), TestConstants.API_TRACE_ID);
    }

    @Test
    public void searchBookByIsbn_success() throws LibraryResourceNotFoundException {

        String isbn = TestConstants.TEST_BOOK_ISBN + "100";
        BookEntity book = new BookEntity(isbn, TestConstants.TEST_BOOK_TITLE,
                TestConstants.TEST_BOOK_YEAR_PUBLISHED, TestConstants.TEST_BOOK_EDITION);

        when(bookRepository.findByIsbn(isbn)).thenReturn(book);
        when(bookStatusRepository.findById(anyInt())).thenReturn(createBookStatusEntityOptional(1));
        Book booksSearched = bookService.searchBookByIsbn(isbn, TestConstants.API_TRACE_ID);

        assertEquals(isbn, booksSearched.getIsbn());
        assertEquals(TestConstants.TEST_BOOK_TITLE, booksSearched.getTitle());
        assertEquals(TestConstants.TEST_BOOK_YEAR_PUBLISHED, booksSearched.getYearPublished().intValue());
        assertEquals(TestConstants.TEST_BOOK_EDITION, booksSearched.getEdition());
    }

    @Test
    public void searchBookByTitle_success() throws LibraryResourceNotFoundException {

        List<BookEntity> booksAdded = Arrays.asList(
                new BookEntity(TestConstants.TEST_BOOK_ISBN + "100", TestConstants.TEST_BOOK_TITLE,
                        TestConstants.TEST_BOOK_YEAR_PUBLISHED + 1, TestConstants.TEST_BOOK_EDITION),
                new BookEntity(TestConstants.TEST_BOOK_ISBN + "101", TestConstants.TEST_BOOK_TITLE,
                        TestConstants.TEST_BOOK_YEAR_PUBLISHED + 2, "Second Edition"),
                new BookEntity(TestConstants.TEST_BOOK_ISBN + "102", TestConstants.TEST_BOOK_TITLE,
                        TestConstants.TEST_BOOK_YEAR_PUBLISHED + 3, "Third Edition"));

        when(bookRepository.findByTitleContaining(TestConstants.TEST_BOOK_TITLE)).thenReturn(booksAdded);
        List<Book> booksSearched = bookService.searchBookByTitle(TestConstants.TEST_BOOK_TITLE, TestConstants.API_TRACE_ID);

        assertEquals(booksAdded.size(), booksSearched.size());
        assertEquals(booksAdded.size(), booksSearched.stream()
                .filter(book -> book.getIsbn().contains(TestConstants.TEST_BOOK_ISBN))
                .count());
    }

    @Test(expected = LibraryResourceNotFoundException.class)
    public void searchBookByIsbn_failure_books_not_found() throws LibraryResourceNotFoundException {

        when(bookRepository.findByIsbn(TestConstants.TEST_BOOK_TITLE)).thenReturn(null);
        bookService.searchBookByTitle(TestConstants.TEST_BOOK_TITLE, TestConstants.API_TRACE_ID);

    }

    @Test(expected = LibraryResourceNotFoundException.class)
    public void searchBookByTitle_failure_books_not_found() throws LibraryResourceNotFoundException {

        when(bookRepository.findByTitleContaining(TestConstants.TEST_BOOK_TITLE)).thenReturn(Collections.emptyList());
        bookService.searchBookByTitle(TestConstants.TEST_BOOK_TITLE, TestConstants.API_TRACE_ID);

    }

    private BookEntity createBookEntity() {
        return new BookEntity(TestConstants.TEST_BOOK_ISBN, TestConstants.TEST_BOOK_TITLE,
                TestConstants.TEST_BOOK_YEAR_PUBLISHED, TestConstants.TEST_BOOK_EDITION);
    }

    private Optional<BookEntity> createBookEntityOptional() {
        return Optional.of(createBookEntity());
    }

    private BookStatusEntity createBookStatusEntity(int bookId) {
        return new BookStatusEntity(bookId, BookStatusState.Active, 3, 0);
    }

    private Optional<BookStatusEntity> createBookStatusEntityOptional(int bookId) {
        return Optional.of(createBookStatusEntity(bookId));
    }
}