package com.skb.course.apis.libraryapis.service;

import com.skb.course.apis.libraryapis.LibraryApiTestUtil;
import com.skb.course.apis.libraryapis.TestConstants;
import com.skb.course.apis.libraryapis.entity.AuthorEntity;
import com.skb.course.apis.libraryapis.entity.BookEntity;
import com.skb.course.apis.libraryapis.entity.BookStatusEntity;
import com.skb.course.apis.libraryapis.entity.PublisherEntity;
import com.skb.course.apis.libraryapis.exception.LibraryResourceAlreadyExistException;
import com.skb.course.apis.libraryapis.exception.LibraryResourceNotFoundException;
import com.skb.course.apis.libraryapis.model.*;
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
import java.util.*;

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

    @Mock
    private PublisherService publisherService;

    @Before
    public void setUp() {
        bookService = new BookService(bookRepository, bookStatusRepository, authorRepository, publisherRepository);
    }

    @Test
    public void addBook_success() throws LibraryResourceNotFoundException {
        when(bookRepository.save(any(BookEntity.class))).thenReturn(LibraryApiTestUtil.createBookEntity());
        when(publisherRepository.findById(anyInt())).thenReturn(Optional.of(new PublisherEntity()));
        Book book = bookService.addBook(LibraryApiTestUtil.createBook(0), TestConstants.API_TRACE_ID);
        assertNotNull(book);
        assertNotNull(book.getBookId());
        assertTrue(book.getIsbn().contains(TestConstants.TEST_BOOK_ISBN));
        assertTrue(book.getTitle().contains(TestConstants.TEST_BOOK_TITLE));
    }

    @Test
    public void getBook_success() throws Exception {

        Optional<BookEntity> bookEntityOptional = LibraryApiTestUtil.createBookEntityOptional();
        when(bookRepository.findById(anyInt())).thenReturn(bookEntityOptional);
        when(bookStatusRepository.findById(anyInt())).thenReturn(Optional.of(new BookStatusEntity(0, BookStatusState.Active, 3, 0)));
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

        BookEntity bookEntity = LibraryApiTestUtil.createBookEntity();
        when(bookRepository.save(any(BookEntity.class))).thenReturn(bookEntity);
        when(publisherRepository.findById(anyInt())).thenReturn(Optional.of(new PublisherEntity()));
        when(bookStatusRepository.findById(anyInt())).thenReturn(Optional.of(new BookStatusEntity(0, BookStatusState.Active, 3, 0)));
        Book book = bookService.addBook(LibraryApiTestUtil.createBook(1), TestConstants.API_TRACE_ID);
        assertNotNull(book);
        assertNotNull(book.getBookId());

        int updatedYearPublished = book.getYearPublished() + 1;
        book.setYearPublished(updatedYearPublished);

        when(bookRepository.findById(anyInt())).thenReturn(LibraryApiTestUtil.createBookEntityOptional());

        book = bookService.updateBook(book, TestConstants.API_TRACE_ID);
        assertEquals(TestConstants.TEST_BOOK_ISBN, book.getIsbn());
        assertEquals(updatedYearPublished, book.getYearPublished().intValue());

    }

    @Test(expected = LibraryResourceNotFoundException.class)
    public void updateBook_failure_book_not_found() throws LibraryResourceNotFoundException {

        Book book = bookService.addBook(LibraryApiTestUtil.createBook(1), TestConstants.API_TRACE_ID);
        assertNotNull(book);
        assertNotNull(book.getBookId());

        book.setYearPublished(book.getYearPublished() + 1);

        when(bookRepository.findById(anyInt())).thenReturn(Optional.empty());
        bookService.updateBook(book, TestConstants.API_TRACE_ID);
    }

    @Test
    public void deleteBook_success() throws LibraryResourceNotFoundException {

        when(bookRepository.save(any(BookEntity.class))).thenReturn(LibraryApiTestUtil.createBookEntity());
        when(publisherRepository.findById(anyInt())).thenReturn(Optional.of(new PublisherEntity()));
        Book book = bookService.addBook(LibraryApiTestUtil.createBook(1), TestConstants.API_TRACE_ID);
        assertNotNull(book);

        doNothing().when(bookRepository).deleteById(anyInt());
        bookService.deleteBook(book.getBookId(), TestConstants.API_TRACE_ID);
    }

    @Test(expected = LibraryResourceNotFoundException.class)
    public void deleteBook_failure_book_not_found() throws LibraryResourceNotFoundException {

        Book book = bookService.addBook(LibraryApiTestUtil.createBook(1), TestConstants.API_TRACE_ID);
        assertNotNull(book);

        doThrow(EmptyResultDataAccessException.class).when(bookRepository).deleteById(anyInt());
        bookService.deleteBook(book.getBookId(), TestConstants.API_TRACE_ID);
    }

    @Test
    public void addBookAuthors_success() throws LibraryResourceNotFoundException {

        BookEntity bookEntity = LibraryApiTestUtil.createBookEntity();
        when(bookRepository.save(any(BookEntity.class))).thenReturn(bookEntity);
        when(publisherRepository.findById(anyInt())).thenReturn(Optional.of(new PublisherEntity()));
        when(bookStatusRepository.findById(anyInt())).thenReturn(Optional.of(new BookStatusEntity(0, BookStatusState.Active, 3, 0)));
        Book book = bookService.addBook(LibraryApiTestUtil.createBook(1), TestConstants.API_TRACE_ID);
        assertNotNull(book);
        assertNotNull(book.getBookId());
        when(bookRepository.findById(anyInt())).thenReturn(LibraryApiTestUtil.createBookEntityOptional());

        Set<Integer> authors = new HashSet<>(1);
        authors.add(1);
        when(authorRepository.findById(anyInt())).thenReturn(LibraryApiTestUtil.createAuthorEntityOptional());
        book = bookService.addBookAuhors(book.getBookId(), authors, TestConstants.API_TRACE_ID);
        assertEquals(TestConstants.TEST_BOOK_ISBN, book.getIsbn());
        assertEquals(1, book.getAuthors().size());
        assertTrue(
                book.getAuthors().stream().
                        allMatch(author -> author.getFirstName().contains(TestConstants.TEST_AUTHOR_FIRST_NAME))
        );
    }

    @Test(expected = LibraryResourceNotFoundException.class)
    public void addBookAuthors_failure_author_not_found() throws LibraryResourceNotFoundException {

        BookEntity bookEntity = LibraryApiTestUtil.createBookEntity();
        when(bookRepository.save(any(BookEntity.class))).thenReturn(bookEntity);
        when(publisherRepository.findById(anyInt())).thenReturn(Optional.of(new PublisherEntity()));
        Book book = bookService.addBook(LibraryApiTestUtil.createBook(1), TestConstants.API_TRACE_ID);
        assertNotNull(book);
        assertNotNull(book.getBookId());
        when(bookRepository.findById(anyInt())).thenReturn(LibraryApiTestUtil.createBookEntityOptional());

        Set<Integer> authors = new HashSet<>(1);
        authors.add(1);
        when(authorRepository.findById(anyInt())).thenReturn(Optional.empty());
        bookService.addBookAuhors(book.getBookId(), authors, TestConstants.API_TRACE_ID);
    }

    @Test
    public void searchBookByIsbn_success() throws LibraryResourceNotFoundException {

        String isbn = TestConstants.TEST_BOOK_ISBN + "100";
        BookEntity book = new BookEntity(isbn, TestConstants.TEST_BOOK_TITLE,
                TestConstants.TEST_BOOK_YEAR_PUBLISHED, TestConstants.TEST_BOOK_EDITION);
        book.setPublisher(LibraryApiTestUtil.createPublisherEntity());

        when(bookRepository.findByIsbn(isbn)).thenReturn(book);
        when(bookStatusRepository.findById(anyInt())).thenReturn(LibraryApiTestUtil.createBookStatusEntityOptional(1));
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

        booksAdded.stream().forEach(bookEntity -> bookEntity.setPublisher(LibraryApiTestUtil.createPublisherEntity()));
        when(bookStatusRepository.findById(anyInt())).thenReturn(LibraryApiTestUtil.createBookStatusEntityOptional(1));
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
        bookService.searchBookByIsbn(TestConstants.TEST_BOOK_TITLE, TestConstants.API_TRACE_ID);

    }

    @Test(expected = LibraryResourceNotFoundException.class)
    public void searchBookByTitle_failure_books_not_found() throws LibraryResourceNotFoundException {

        when(bookRepository.findByTitleContaining(TestConstants.TEST_BOOK_TITLE)).thenReturn(Collections.emptyList());
        bookService.searchBookByTitle(TestConstants.TEST_BOOK_TITLE, TestConstants.API_TRACE_ID);

    }


}