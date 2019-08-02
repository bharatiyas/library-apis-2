package com.skb.course.apis.libraryapis.service;

import com.skb.course.apis.libraryapis.LibraryApiTestUtil;
import com.skb.course.apis.libraryapis.TestConstants;
import com.skb.course.apis.libraryapis.entity.AuthorEntity;
import com.skb.course.apis.libraryapis.exception.LibraryResourceAlreadyExistException;
import com.skb.course.apis.libraryapis.exception.LibraryResourceNotFoundException;
import com.skb.course.apis.libraryapis.model.Author;
import com.skb.course.apis.libraryapis.model.Gender;
import com.skb.course.apis.libraryapis.repository.AuthorRepository;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AuthorServiceTest {

    @Mock
    AuthorRepository authorRepository;

    AuthorService authorService;

    @Before
    public void setUp() {
        authorService = new AuthorService(authorRepository);
    }

    @Test
    public void addAuthor_success() throws LibraryResourceAlreadyExistException {
        when(authorRepository.save(any(AuthorEntity.class))).thenReturn(createAuthorEntity());
        Author author = authorService.addAuthor(LibraryApiTestUtil.createAuthor(), TestConstants.API_TRACE_ID);
        assertNotNull(author);
        assertNotNull(author.getAuthorId());
        assertTrue(author.getFirstName().contains(TestConstants.TEST_AUTHOR_FIRST_NAME));
        assertTrue(author.getLastName().contains(TestConstants.TEST_AUTHOR_LAST_NAME));
    }

    @Test
    public void getAuthor_success() throws Exception {

        when(authorRepository.findById(anyInt())).thenReturn(createAuthorEntityOptional());
        Author author = authorService.getAuthor(123, TestConstants.API_TRACE_ID);

        assertNotNull(author);
        assertNotNull(author.getAuthorId());
        assertEquals(TestConstants.TEST_AUTHOR_FIRST_NAME, author.getFirstName());
        assertEquals(TestConstants.TEST_AUTHOR_LAST_NAME, author.getLastName());

    }

    @Test(expected = LibraryResourceNotFoundException.class)
    public void getAuthor_failure_author_not_found() throws Exception {

        when(authorRepository.findById(anyInt())).thenReturn(Optional.empty());
        authorService.getAuthor(123, TestConstants.API_TRACE_ID);

    }

    @Test
    public void updateAuthor_success() throws LibraryResourceAlreadyExistException, LibraryResourceNotFoundException {

        AuthorEntity authorEntity = createAuthorEntity();
        when(authorRepository.save(any(AuthorEntity.class))).thenReturn(authorEntity);

        Author author = authorService.addAuthor(LibraryApiTestUtil.createAuthor(), TestConstants.API_TRACE_ID);
        assertNotNull(author);
        assertNotNull(author.getAuthorId());

        LocalDate updatedDob = author.getDateOfBirth().minusMonths(5);
        author.setDateOfBirth(updatedDob);

        when(authorRepository.findById(anyInt())).thenReturn(createAuthorEntityOptional());

        author = authorService.updateAuthor(author, TestConstants.API_TRACE_ID);
        assertEquals(TestConstants.TEST_AUTHOR_FIRST_NAME, author.getFirstName());
        assertTrue(author.getDateOfBirth().isEqual(updatedDob));

    }

    @Test(expected = LibraryResourceNotFoundException.class)
    public void updateAuthor_failure_author_not_found() throws LibraryResourceAlreadyExistException, LibraryResourceNotFoundException {

        AuthorEntity authorEntity = createAuthorEntity();
        when(authorRepository.save(any(AuthorEntity.class))).thenReturn(authorEntity);

        Author author = authorService.addAuthor(LibraryApiTestUtil.createAuthor(), TestConstants.API_TRACE_ID);
        assertNotNull(author);
        assertNotNull(author.getAuthorId());

        LocalDate updatedDob = author.getDateOfBirth().minusMonths(5);
        author.setDateOfBirth(updatedDob);

        when(authorRepository.findById(anyInt())).thenReturn(Optional.empty());
        authorService.updateAuthor(author, TestConstants.API_TRACE_ID);

    }

    @Test
    public void deleteAuthor_success() throws LibraryResourceAlreadyExistException, LibraryResourceNotFoundException {

        when(authorRepository.save(any(AuthorEntity.class))).thenReturn(createAuthorEntity());
        Author author = authorService.addAuthor(LibraryApiTestUtil.createAuthor(), TestConstants.API_TRACE_ID);
        assertNotNull(author);

        doNothing().when(authorRepository).deleteById(anyInt());
        authorService.deleteAuthor(author.getAuthorId(), TestConstants.API_TRACE_ID);
    }

    @Test(expected = LibraryResourceNotFoundException.class)
    public void deleteAuthor_failure_author_not_found() throws LibraryResourceAlreadyExistException, LibraryResourceNotFoundException {

        when(authorRepository.save(any(AuthorEntity.class))).thenReturn(createAuthorEntity());
        Author author = authorService.addAuthor(LibraryApiTestUtil.createAuthor(), TestConstants.API_TRACE_ID);
        assertNotNull(author);

        doThrow(EmptyResultDataAccessException.class).when(authorRepository).deleteById(anyInt());
        authorService.deleteAuthor(author.getAuthorId(), TestConstants.API_TRACE_ID);
    }

    @Test
    public void searchAuthors_success_firstname_lastname() throws LibraryResourceNotFoundException {

        List<AuthorEntity> authorsAdded = Arrays.asList(
                new AuthorEntity(TestConstants.TEST_AUTHOR_FIRST_NAME + "a", TestConstants.TEST_AUTHOR_LAST_NAME, LocalDate.now().minusYears(30), Gender.Female),
                new AuthorEntity(TestConstants.TEST_AUTHOR_FIRST_NAME + "b", TestConstants.TEST_AUTHOR_LAST_NAME, LocalDate.now().minusYears(32), Gender.Male),
                new AuthorEntity(TestConstants.TEST_AUTHOR_FIRST_NAME + "c", TestConstants.TEST_AUTHOR_LAST_NAME, LocalDate.now().minusYears(33), Gender.Female));

        when(authorRepository.findByFirstNameAndLastNameContaining(TestConstants.TEST_AUTHOR_FIRST_NAME, TestConstants.TEST_AUTHOR_LAST_NAME)).thenReturn(authorsAdded);
        List<Author> authorsSearched = authorService.searchAuthors(TestConstants.TEST_AUTHOR_FIRST_NAME, TestConstants.TEST_AUTHOR_LAST_NAME, TestConstants.API_TRACE_ID);

        assertEquals(authorsAdded.size(), authorsSearched.size());
        assertEquals(authorsAdded.size(), authorsSearched.stream()
                .filter(author -> author.getFirstName().contains(TestConstants.TEST_AUTHOR_FIRST_NAME))
                .count());
    }

    @Test
    public void searchAuthors_success_firstname() throws LibraryResourceNotFoundException {

        List<AuthorEntity> authorsAdded = Arrays.asList(
                new AuthorEntity(TestConstants.TEST_AUTHOR_FIRST_NAME + "a", TestConstants.TEST_AUTHOR_LAST_NAME, LocalDate.now().minusYears(30), Gender.Female),
                new AuthorEntity(TestConstants.TEST_AUTHOR_FIRST_NAME + "b", TestConstants.TEST_AUTHOR_LAST_NAME, LocalDate.now().minusYears(32), Gender.Male),
                new AuthorEntity(TestConstants.TEST_AUTHOR_FIRST_NAME + "c", TestConstants.TEST_AUTHOR_LAST_NAME, LocalDate.now().minusYears(33), Gender.Female),
                new AuthorEntity(TestConstants.TEST_AUTHOR_FIRST_NAME + "d", TestConstants.TEST_AUTHOR_LAST_NAME, LocalDate.now().minusYears(40), Gender.Female));

        when(authorRepository.findByFirstNameContaining(TestConstants.TEST_AUTHOR_FIRST_NAME)).thenReturn(authorsAdded);
        List<Author> authorsSearched = authorService.searchAuthors(TestConstants.TEST_AUTHOR_FIRST_NAME, "", TestConstants.API_TRACE_ID);

        assertEquals(authorsAdded.size(), authorsSearched.size());
        assertEquals(authorsAdded.size(), authorsSearched.stream()
                .filter(author -> author.getFirstName().contains(TestConstants.TEST_AUTHOR_FIRST_NAME))
                .count());
    }

    @Test
    public void searchAuthors_success_lastname() throws LibraryResourceNotFoundException {

        List<AuthorEntity> authorsAdded = Arrays.asList(
                new AuthorEntity(TestConstants.TEST_AUTHOR_FIRST_NAME + "a", TestConstants.TEST_AUTHOR_LAST_NAME, LocalDate.now().minusYears(30), Gender.Female),
                new AuthorEntity(TestConstants.TEST_AUTHOR_FIRST_NAME + "b", TestConstants.TEST_AUTHOR_LAST_NAME, LocalDate.now().minusYears(32), Gender.Male),
                new AuthorEntity(TestConstants.TEST_AUTHOR_FIRST_NAME + "c", TestConstants.TEST_AUTHOR_LAST_NAME, LocalDate.now().minusYears(33), Gender.Female),
                new AuthorEntity(TestConstants.TEST_AUTHOR_FIRST_NAME + "d", TestConstants.TEST_AUTHOR_LAST_NAME, LocalDate.now().minusYears(40), Gender.Female));

        when(authorRepository.findByLastNameContaining(TestConstants.TEST_AUTHOR_LAST_NAME)).thenReturn(authorsAdded);
        List<Author> authorsSearched = authorService.searchAuthors("", TestConstants.TEST_AUTHOR_LAST_NAME, TestConstants.API_TRACE_ID);

        assertEquals(authorsAdded.size(), authorsSearched.size());
        assertEquals(authorsAdded.size(), authorsSearched.stream()
                .filter(author -> author.getFirstName().contains(TestConstants.TEST_AUTHOR_FIRST_NAME))
                .count());
    }

    @Test(expected = LibraryResourceNotFoundException.class)
    public void searchAuthors_failure_authors_not_found() throws LibraryResourceNotFoundException {

        when(authorRepository.findByFirstNameAndLastNameContaining(TestConstants.TEST_AUTHOR_FIRST_NAME, TestConstants.TEST_AUTHOR_LAST_NAME)).thenReturn(Collections.emptyList());
        authorService.searchAuthors(TestConstants.TEST_AUTHOR_FIRST_NAME, TestConstants.TEST_AUTHOR_LAST_NAME, TestConstants.API_TRACE_ID);

    }

    private AuthorEntity createAuthorEntity() {
        return new AuthorEntity(TestConstants.TEST_AUTHOR_FIRST_NAME, TestConstants.TEST_AUTHOR_LAST_NAME,
                LocalDate.now().minusYears(30), Gender.Female);
    }

    private Optional<AuthorEntity> createAuthorEntityOptional() {
        return Optional.of(createAuthorEntity());
    }
}