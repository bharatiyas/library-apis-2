package com.skb.course.apis.libraryapis.controller;

import com.skb.course.apis.libraryapis.TestConstants;
import com.skb.course.apis.libraryapis.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;

@Component
public class LibraryApiIntegrationTestUtil {

    @Autowired
    private TestRestTemplate testRestTemplate;

    private static int userCtr;
    private static int authorCtr;
    private static int publisherCtr;
    private static int bookCtr;

    @Value("${library.api.user.admin.username}")
    private String adminUsername;

    @Value("${library.api.user.admin.password}")
    private String adminPassword;

    private ResponseEntity<String> adminLoginResponse = null;

    public ResponseEntity<String> loginUser(String username, String password) {

        if(username.equals("adminUsername") && (adminLoginResponse != null)) {
            return adminLoginResponse;
        }

        String loginUrl = TestConstants.LOGIN_URL;
        URI loginUri = null;
        try {
            loginUri = new URI(loginUrl);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        HttpEntity<String> loginRequest = new HttpEntity<>(createLoginBody(username, password));
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(loginUri, loginRequest, String.class);

        if(username.equals("adminUsername")) {
            adminLoginResponse = responseEntity;
        }

        return responseEntity;
    }

    public ResponseEntity<LibraryUser> registerNewUser(String username) {

        userCtr++;
        Gender gender = userCtr % 2 == 1 ? Gender.Male : Gender.Female;
        URI registerUri = null;
        try {
            registerUri = new URI(TestConstants.USER_API_REGISTER_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        LibraryUser newUser = new LibraryUser(username, TestConstants.TEST_USER_FIRST_NAME,
                TestConstants.TEST_USER_LAST_NAME, LocalDate.now().minusYears(30 + userCtr), gender, "123445556",
                username + "@email.com");

        HttpEntity<LibraryUser> newUserRequest = new HttpEntity<>(newUser);

        return testRestTemplate.postForEntity(registerUri, newUserRequest, LibraryUser.class);
    }

    public ResponseEntity<Author> addNewAuthor(MultiValueMap<String, String> headers) {

        authorCtr++;
        Gender gender = authorCtr % 2 == 1 ? Gender.Male : Gender.Female;
        URI authorUri = null;
        try {
            authorUri = new URI(TestConstants.AUTHOR_API_BASE_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // Create a new Author object
        Author newAuthor = new Author(TestConstants.TEST_AUTHOR_FIRST_NAME + authorCtr,
                TestConstants.TEST_AUTHOR_LAST_NAME, LocalDate.now().minusYears(30 + authorCtr), gender);

        // Add Authorization Token and create request entity
        HttpEntity<Author> newAuthorRequest = new HttpEntity<>(newAuthor, headers);

        // Finally send the request
        return testRestTemplate.exchange(authorUri, HttpMethod.POST, newAuthorRequest, Author.class);

    }

    public ResponseEntity<Publisher> addNewPublisher(MultiValueMap<String, String> headers) {

        publisherCtr++;
        URI publisherUri = null;
        try {
            publisherUri = new URI(TestConstants.PUBLISHER_API_BASE_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // Create a new Publisher object
        Publisher newPublisher = new Publisher(TestConstants.TEST_PUBLISHER_NAME + publisherCtr,
                TestConstants.TEST_PUBLISHER_EMAIL, "12344556");

        // Add Authorization Token and create request entity
        HttpEntity<Publisher> newPublisherRequest = new HttpEntity<>(newPublisher, headers);

        // Finally send the request
        return testRestTemplate.exchange(publisherUri, HttpMethod.POST, newPublisherRequest, Publisher.class);

    }

    public ResponseEntity<Book> addNewBook(MultiValueMap<String, String> headers, int publisherId) {

        bookCtr++;
        URI publisherUri = null;
        try {
            publisherUri = new URI(TestConstants.BOOK_API_BASE_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // String isbn, String title, int publisherId, int yearPublished, String edition, BookStatus bookStatus
        // Create a new Publisher object
        Book newBook = new Book(TestConstants.TEST_BOOK_ISBN + bookCtr,
                TestConstants.TEST_BOOK_TITLE + "-" + bookCtr, publisherId,
                TestConstants.TEST_BOOK_YEAR_PUBLISHED, TestConstants.TEST_BOOK_EDITION, createBookStatus());

        // Add Authorization Token and create request entity
        HttpEntity<Book> newBookRequest = new HttpEntity<>(newBook, headers);

        // Finally send the request
        return testRestTemplate.exchange(publisherUri, HttpMethod.POST, newBookRequest, Book.class);

    }

    private String createLoginBody(String username, String password) {
        return "{ \"username\": \"" + username + "\", \"password\": \"" +  password + "\"}";
    }

    private BookStatus createBookStatus() {
        return new BookStatus(BookStatusState.Active, 5, 0);
    }
}
