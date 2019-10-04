package com.skb.course.apis.libraryapis.user;

import com.skb.course.apis.libraryapis.testutils.LibraryApiTestUtil;
import com.skb.course.apis.libraryapis.testutils.TestConstants;
import com.skb.course.apis.libraryapis.book.BookEntity;
import com.skb.course.apis.libraryapis.book.BookRepository;
import com.skb.course.apis.libraryapis.book.BookService;
import com.skb.course.apis.libraryapis.book.BookStatusRepository;
import com.skb.course.apis.libraryapis.publisher.PublisherRepository;
import com.skb.course.apis.libraryapis.exception.LibraryResourceAlreadyExistException;
import com.skb.course.apis.libraryapis.exception.LibraryResourceNotFoundException;
import com.skb.course.apis.libraryapis.exception.UserNotFoundException;
import com.skb.course.apis.libraryapis.book.Book;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookStatusRepository bookStatusRepository;

    @Mock
    private BookService bookService;

    @Mock
    private PublisherRepository publisherRepository;

    @Mock
    private UserBookEntityRepository userBookEntityRepository;

    UserService userService;

    @Before
    public void setUp() {

        userService = new UserService(bCryptPasswordEncoder, userRepository,
                bookRepository, bookStatusRepository,
                bookService, userBookEntityRepository);
    }

    @Test
    public void addUser_success() throws LibraryResourceAlreadyExistException {
        when(userRepository.save(any(UserEntity.class))).thenReturn(LibraryApiTestUtil.createUserEntity(TestConstants.TEST_USER_USERNAME));
        LibraryUser user = userService.addUser(LibraryApiTestUtil.createUser(TestConstants.TEST_USER_USERNAME), TestConstants.API_TRACE_ID);
        assertNotNull(user);
        assertNotNull(user.getUserId());
        assertEquals(TestConstants.TEST_USER_USERNAME, user.getUsername());
        assertTrue(user.getFirstName().contains(TestConstants.TEST_USER_FIRST_NAME));
        assertTrue(user.getLastName().contains(TestConstants.TEST_USER_LAST_NAME));
    }

    @Test
    public void getUserByUserId_success() throws Exception {

        when(userRepository.findById(anyInt())).thenReturn(LibraryApiTestUtil.createUserEntityOptional(TestConstants.TEST_USER_USERNAME));
        LibraryUser user = userService.getUserByUserId(123, TestConstants.API_TRACE_ID);

        assertNotNull(user);
        assertNotNull(user.getUserId());
        assertEquals(TestConstants.TEST_USER_FIRST_NAME, user.getFirstName());
        assertEquals(TestConstants.TEST_USER_LAST_NAME, user.getLastName());

    }

    @Test(expected = LibraryResourceNotFoundException.class)
    public void getUserByUserId_failure_author_not_found() throws Exception {

        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
        userService.getUserByUserId(123, TestConstants.API_TRACE_ID);

    }

    @Test
    public void getUserByUsername_success() throws Exception {

        when(userRepository.findByUsername(any(String.class))).thenReturn(LibraryApiTestUtil.createUserEntity(TestConstants.TEST_USER_USERNAME));
        LibraryUser user = userService.getUserByUsername(TestConstants.TEST_USER_USERNAME);

        assertNotNull(user);
        assertNotNull(user.getUserId());
        assertEquals(TestConstants.TEST_USER_USERNAME, user.getUsername());
        assertEquals(TestConstants.TEST_USER_FIRST_NAME, user.getFirstName());
        assertEquals(TestConstants.TEST_USER_LAST_NAME, user.getLastName());

    }

    @Test(expected = UserNotFoundException.class)
    public void getUserByUsername_failure_user_not_found() throws Exception {

        when(userRepository.findByUsername(any(String.class))).thenReturn(null);
        userService.getUserByUsername(TestConstants.TEST_USER_USERNAME);

    }

    @Test
    public void updateUser_success() throws LibraryResourceAlreadyExistException, LibraryResourceNotFoundException {

        UserEntity authorEntity = LibraryApiTestUtil.createUserEntity(TestConstants.TEST_USER_USERNAME);
        when(userRepository.save(any(UserEntity.class))).thenReturn(authorEntity);

        LibraryUser libraryUser = userService.addUser(LibraryApiTestUtil.createUser(TestConstants.TEST_USER_USERNAME), TestConstants.API_TRACE_ID);
        assertNotNull(libraryUser);
        assertNotNull(libraryUser.getUserId());

        libraryUser.setEmailId("changed.email@email.con");
        libraryUser.setPhoneNumber("987654321");
        libraryUser.setPassword("ChangedPassword");

        when(userRepository.findById(anyInt())).thenReturn(LibraryApiTestUtil.createUserEntityOptional(TestConstants.TEST_USER_USERNAME));

        libraryUser = userService.updateUser(libraryUser, TestConstants.API_TRACE_ID);
        assertEquals(TestConstants.TEST_USER_FIRST_NAME, libraryUser.getFirstName());
        assertTrue(libraryUser.getEmailId().equals("changed.email@email.con"));
        assertTrue(libraryUser.getPhoneNumber().equals("987654321"));
    }

    @Test(expected = LibraryResourceNotFoundException.class)
    public void updateUser_failure_user_not_found() throws LibraryResourceAlreadyExistException, LibraryResourceNotFoundException {

        UserEntity authorEntity = LibraryApiTestUtil.createUserEntity(TestConstants.TEST_USER_USERNAME);
        when(userRepository.save(any(UserEntity.class))).thenReturn(authorEntity);

        LibraryUser libraryUser = userService.addUser(LibraryApiTestUtil.createUser(TestConstants.TEST_USER_USERNAME), TestConstants.API_TRACE_ID);
        assertNotNull(libraryUser);
        assertNotNull(libraryUser.getUserId());

        libraryUser.setEmailId("changed.email@email.con");
        libraryUser.setPhoneNumber("987654321");
        libraryUser.setPassword("ChangedPassword");

        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
        userService.updateUser(libraryUser, TestConstants.API_TRACE_ID);
    }


    @Test
    public void deleteUserByUserId_success() throws LibraryResourceAlreadyExistException, LibraryResourceNotFoundException {

        when(userRepository.save(any(UserEntity.class))).thenReturn(LibraryApiTestUtil.createUserEntity(TestConstants.TEST_USER_USERNAME));
        LibraryUser libraryUser = userService.addUser(LibraryApiTestUtil.createUser(TestConstants.TEST_USER_USERNAME), TestConstants.API_TRACE_ID);
        assertNotNull(libraryUser);

        doNothing().when(userRepository).deleteById(anyInt());
        userService.deleteUserByUserId(libraryUser.getUserId(), TestConstants.API_TRACE_ID);
    }

    @Test(expected = LibraryResourceNotFoundException.class)
    public void deleteUser_failure_user_not_found() throws LibraryResourceAlreadyExistException, LibraryResourceNotFoundException {

        when(userRepository.save(any(UserEntity.class))).thenReturn(LibraryApiTestUtil.createUserEntity(TestConstants.TEST_USER_USERNAME));
        LibraryUser libraryUser = userService.addUser(LibraryApiTestUtil.createUser(TestConstants.TEST_USER_USERNAME), TestConstants.API_TRACE_ID);
        assertNotNull(libraryUser);

        doThrow(EmptyResultDataAccessException.class).when(userRepository).deleteById(anyInt());
        userService.deleteUserByUserId(libraryUser.getUserId(), TestConstants.API_TRACE_ID);
    }

    @Test
    public void searchUsers_success_firstname_lastname() throws LibraryResourceNotFoundException {

        List<UserEntity> authorsAdded = Arrays.asList(
                LibraryApiTestUtil.createUserEntity(TestConstants.TEST_USER_USERNAME + ".1"),
                LibraryApiTestUtil.createUserEntity(TestConstants.TEST_USER_USERNAME + ".2"),
                LibraryApiTestUtil.createUserEntity(TestConstants.TEST_USER_USERNAME + ".3"));

        when(userRepository.findByFirstNameAndLastNameContaining(TestConstants.TEST_USER_FIRST_NAME, TestConstants.TEST_USER_LAST_NAME)).thenReturn(authorsAdded);
        List<LibraryUser> usersSearched = userService.searchUsers(TestConstants.TEST_USER_FIRST_NAME, TestConstants.TEST_USER_LAST_NAME, TestConstants.API_TRACE_ID);

        assertEquals(authorsAdded.size(), usersSearched.size());
        assertEquals(authorsAdded.size(), usersSearched.stream()
                .filter(user -> user.getFirstName().contains(TestConstants.TEST_USER_FIRST_NAME))
                .count());
    }


    @Test
    public void searchUsers_success_firstname() throws LibraryResourceNotFoundException {

        List<UserEntity> usersAdded = Arrays.asList(
                LibraryApiTestUtil.createUserEntity(TestConstants.TEST_USER_USERNAME + ".1"),
                LibraryApiTestUtil.createUserEntity(TestConstants.TEST_USER_USERNAME + ".2"),
                LibraryApiTestUtil.createUserEntity(TestConstants.TEST_USER_USERNAME + ".3"),
                new UserEntity(TestConstants.TEST_USER_USERNAME + ".4", TestConstants.TEST_USER_PASSWORD, TestConstants.TEST_USER_FIRST_NAME,
                        "MismatchLn", LocalDate.now().minusYears(20), TestConstants.TEST_USER_GENDER,
                        TestConstants.TEST_USER_PHONE, TestConstants.TEST_USER_EMAIL, "USER")
                );

        when(userRepository.findByFirstNameContaining(TestConstants.TEST_USER_FIRST_NAME)).thenReturn(usersAdded);
        List<LibraryUser> usersSearched = userService.searchUsers(TestConstants.TEST_USER_FIRST_NAME, "", TestConstants.API_TRACE_ID);

        assertEquals(usersAdded.size(), usersSearched.size());
        assertEquals(usersAdded.size(), usersSearched.stream()
                .filter(user -> user.getFirstName().contains(TestConstants.TEST_USER_FIRST_NAME))
                .count());
        assertEquals(usersAdded.size() - 1, usersSearched.stream()
                .filter(user -> user.getLastName().contains(TestConstants.TEST_USER_LAST_NAME))
                .count());
    }

    @Test
    public void searchUsers_success_lastname() throws LibraryResourceNotFoundException {

        List<UserEntity> usersAdded = Arrays.asList(
                LibraryApiTestUtil.createUserEntity(TestConstants.TEST_USER_USERNAME + ".1"),
                LibraryApiTestUtil.createUserEntity(TestConstants.TEST_USER_USERNAME + ".2"),
                LibraryApiTestUtil.createUserEntity(TestConstants.TEST_USER_USERNAME + ".3"),
                new UserEntity(TestConstants.TEST_USER_USERNAME + ".4", TestConstants.TEST_USER_PASSWORD, "MismatchFn",
                        TestConstants.TEST_USER_LAST_NAME, LocalDate.now().minusYears(20), TestConstants.TEST_USER_GENDER,
                        TestConstants.TEST_USER_PHONE, TestConstants.TEST_USER_EMAIL, "USER")
        );

        when(userRepository.findByLastNameContaining(TestConstants.TEST_USER_LAST_NAME)).thenReturn(usersAdded);
        List<LibraryUser> usersSearched = userService.searchUsers("", TestConstants.TEST_USER_LAST_NAME, TestConstants.API_TRACE_ID);

        assertEquals(usersAdded.size(), usersSearched.size());
        assertEquals(usersAdded.size(), usersSearched.stream()
                .filter(user -> user.getLastName().contains(TestConstants.TEST_USER_LAST_NAME))
                .count());
        assertEquals(usersAdded.size() - 1, usersSearched.stream()
                .filter(user -> user.getFirstName().contains(TestConstants.TEST_USER_FIRST_NAME))
                .count());
    }

    @Test
    public void issueBooks_success() throws LibraryResourceNotFoundException, LibraryResourceAlreadyExistException {

        // Add a user
        UserEntity userEntity = LibraryApiTestUtil.createUserEntity(TestConstants.TEST_USER_USERNAME);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        LibraryUser libraryUser = userService.addUser(LibraryApiTestUtil.createUser(TestConstants.TEST_USER_USERNAME), TestConstants.API_TRACE_ID);

        // Add a book
        BookEntity bookEntity = LibraryApiTestUtil.createBookEntity();
        Book book = bookService.addBook(LibraryApiTestUtil.createBook(1), TestConstants.API_TRACE_ID);

        when(bookRepository.findById(anyInt())).thenReturn(LibraryApiTestUtil.createBookEntityOptional());

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(userEntity));

        Set<Integer> books = new HashSet<>(1);
        books.add(1);
        IssueBookResponse issueBookResponse = userService.issueBooks(libraryUser.getUserId(), books, TestConstants.API_TRACE_ID);

        assertNotNull(issueBookResponse);
        assertNotNull(issueBookResponse.getIssueBookStatuses());
        assertEquals(1, issueBookResponse.getIssueBookStatuses().size());
        assertEquals(1, issueBookResponse.getIssueBookStatuses().stream()
                .filter(issueBookStatus -> issueBookStatus.getStatus().equals("Issued"))
                .count()
        );
    }

    @Test(expected = LibraryResourceNotFoundException.class)
    public void issueBooks_failure_book_not_found() throws LibraryResourceNotFoundException, LibraryResourceAlreadyExistException {

        // Add a user
        UserEntity userEntity = LibraryApiTestUtil.createUserEntity(TestConstants.TEST_USER_USERNAME);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        LibraryUser libraryUser = userService.addUser(LibraryApiTestUtil.createUser(TestConstants.TEST_USER_USERNAME), TestConstants.API_TRACE_ID);

        Set<Integer> books = new HashSet<>(1);
        books.add(1);
        IssueBookResponse issueBookResponse = userService.issueBooks(libraryUser.getUserId(), books, TestConstants.API_TRACE_ID);
        assertNotNull(issueBookResponse);
        assertNotNull(issueBookResponse.getIssueBookStatuses());
        assertEquals(1, issueBookResponse.getIssueBookStatuses().size());
        assertEquals(1, issueBookResponse.getIssueBookStatuses().stream()
                .filter(issueBookStatus -> issueBookStatus.getStatus().equals("Not Issued"))
                .count()
        );
        assertEquals(1, issueBookResponse.getIssueBookStatuses().stream()
                .filter(issueBookStatus -> issueBookStatus.getRemarks().equals("Book Not Found"))
                .count()
        );
    }

}