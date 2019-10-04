package com.skb.course.apis.libraryapis.testutils;

import com.skb.course.apis.libraryapis.author.Author;
import com.skb.course.apis.libraryapis.book.Book;
import com.skb.course.apis.libraryapis.publisher.Publisher;
import com.skb.course.apis.libraryapis.user.LibraryUser;
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

@Component
public class LibraryApiIntegrationTestUtil {

    @Autowired
    private TestRestTemplate testRestTemplate;

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

        URI registerUri = null;
        try {
            registerUri = new URI(TestConstants.USER_API_REGISTER_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        HttpEntity<LibraryUser> newUserRequest = new HttpEntity<>(LibraryApiTestUtil.createUser(username));

        return testRestTemplate.postForEntity(registerUri, newUserRequest, LibraryUser.class);
    }

    public ResponseEntity<Author> addNewAuthor(MultiValueMap<String, String> headers) {

        URI authorUri = null;
        try {
            authorUri = new URI(TestConstants.AUTHOR_API_BASE_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // Add Authorization Token and create request entity
        HttpEntity<Author> newAuthorRequest = new HttpEntity<>(LibraryApiTestUtil.createAuthor(), headers);

        // Finally send the request
        return testRestTemplate.exchange(authorUri, HttpMethod.POST, newAuthorRequest, Author.class);
    }

    public ResponseEntity<Publisher> addNewPublisher(MultiValueMap<String, String> headers) {

        URI publisherUri = null;
        try {
            publisherUri = new URI(TestConstants.PUBLISHER_API_BASE_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // Add Authorization Token and create request entity
        HttpEntity<Publisher> newPublisherRequest = new HttpEntity<>(LibraryApiTestUtil.createPublisher(), headers);

        // Finally send the request
        return testRestTemplate.exchange(publisherUri, HttpMethod.POST, newPublisherRequest, Publisher.class);
    }

    public ResponseEntity<Book> addNewBook(MultiValueMap<String, String> headers, int publisherId) {

        URI publisherUri = null;
        try {
            publisherUri = new URI(TestConstants.BOOK_API_BASE_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // Add Authorization Token and create request entity
        HttpEntity<Book> newBookRequest = new HttpEntity<>(LibraryApiTestUtil.createBook(publisherId), headers);

        // Finally send the request
        return testRestTemplate.exchange(publisherUri, HttpMethod.POST, newBookRequest, Book.class);

    }

    private String createLoginBody(String username, String password) {
        return "{ \"username\": \"" + username + "\", \"password\": \"" +  password + "\"}";
    }

}
