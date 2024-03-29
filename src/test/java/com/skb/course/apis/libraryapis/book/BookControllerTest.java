package com.skb.course.apis.libraryapis.book;

import com.skb.course.apis.libraryapis.testutils.TestConstants;
import com.skb.course.apis.libraryapis.author.Author;
import com.skb.course.apis.libraryapis.testutils.LibraryApiIntegrationTestUtil;
import com.skb.course.apis.libraryapis.user.LibraryUser;
import com.skb.course.apis.libraryapis.publisher.Publisher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.net.URISyntaxException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private LibraryApiIntegrationTestUtil libraryApiIntegrationTestUtil;

    @Value("${library.api.user.admin.username}")
    private String adminUsername;

    @Value("${library.api.user.admin.password}")
    private String adminPassword;

    private static int bookCtr;

    @Before
    public void setUp() throws Exception {
    }

    @Autowired
    Environment environment;

    @Test
    public void addBook_success() {

        // Login with the admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        MultiValueMap<String, String> header = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));

        // First we need to add a Publisher because we need to add a book
        ResponseEntity<Publisher> publisherResponseEntity = libraryApiIntegrationTestUtil.addNewPublisher(header);

        // Now add a book
        ResponseEntity<Book> response = libraryApiIntegrationTestUtil.addNewBook(header, publisherResponseEntity.getBody().getPublisherId());
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Book responseBook = response.getBody();
        Assert.assertNotNull(responseBook);
        Assert.assertNotNull(responseBook.getBookId());
        Assert.assertTrue(responseBook.getTitle().contains(TestConstants.TEST_BOOK_TITLE));
        Assert.assertTrue(responseBook.getIsbn().contains(TestConstants.TEST_BOOK_ISBN));
    }

    @Test
    public void getBook_success_admin_user() {

        String port = environment.getProperty("local.server.port");

        // Login with the admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        MultiValueMap<String, String> header = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));

        // First we need to add a Publisher because we need to add a book
        ResponseEntity<Publisher> publisherResponseEntity = libraryApiIntegrationTestUtil.addNewPublisher(header);

        // Now add a book
        ResponseEntity<Book> response = libraryApiIntegrationTestUtil.addNewBook(header, publisherResponseEntity.getBody().getPublisherId());
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Book responseBook = response.getBody();
        Assert.assertNotNull(responseBook);

        // Now we get the Book
        URI getBookUri = null;
        try {
            getBookUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.BOOK_API_BASE_URL + "/" + responseBook.getBookId());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ResponseEntity<Book> libBookResponse = testRestTemplate.exchange(getBookUri, HttpMethod.GET,
                new HttpEntity<Object>(header), Book.class);

        Assert.assertEquals(HttpStatus.OK, libBookResponse.getStatusCode());
        Book getBook = libBookResponse.getBody();
        Assert.assertEquals(responseBook.getBookId(), getBook.getBookId());
        Assert.assertTrue(getBook.getTitle().contains(TestConstants.TEST_BOOK_TITLE));
        Assert.assertTrue(responseBook.getIsbn().contains(TestConstants.TEST_BOOK_ISBN));
    }

    @Test
    public void getBook_success_normal_user() {

        String port = environment.getProperty("local.server.port");

        // Login with the admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        MultiValueMap<String, String> header = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));

        // First we need to add a Publisher because we need to add a book
        ResponseEntity<Publisher> publisherResponseEntity = libraryApiIntegrationTestUtil.addNewPublisher(header);

        // Now add a book
        ResponseEntity<Book> response = libraryApiIntegrationTestUtil.addNewBook(header, publisherResponseEntity.getBody().getPublisherId());
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Book responseBook = response.getBody();
        Assert.assertNotNull(responseBook);

        // Create a normal user
        ResponseEntity<LibraryUser> responseEntity = libraryApiIntegrationTestUtil.registerNewUser("get.book.success.normal.user");
        LibraryUser libraryUser = responseEntity.getBody();

        // Login with normal user credentials
        ResponseEntity<String> userLoginResponse = libraryApiIntegrationTestUtil.loginUser(libraryUser.getUsername(), libraryUser.getPassword());
        header = createAuthorizationHeader(userLoginResponse.getHeaders().get("Authorization").get(0));

        URI getBookUri = null;
        try {
            getBookUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.BOOK_API_BASE_URL + "/" + response.getBody().getBookId());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ResponseEntity<Book> libBookResponse = testRestTemplate.exchange(getBookUri, HttpMethod.GET,
                new HttpEntity<Object>(header), Book.class);

        Assert.assertEquals(HttpStatus.OK, libBookResponse.getStatusCode());
        Book getBook = libBookResponse.getBody();
        Assert.assertEquals(responseBook.getBookId(), getBook.getBookId());
        Assert.assertTrue(getBook.getTitle().contains(TestConstants.TEST_BOOK_TITLE));
        Assert.assertTrue(responseBook.getIsbn().contains(TestConstants.TEST_BOOK_ISBN));
    }


    @Test
    public void getBook_author_not_found() {

        String port = environment.getProperty("local.server.port");

        // Login with the admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        MultiValueMap<String, String> header = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));

        // First we need to add a Publisher because we need to add a book
        ResponseEntity<Publisher> publisherResponseEntity = libraryApiIntegrationTestUtil.addNewPublisher(header);

        // Now add a book
        ResponseEntity<Book> response = libraryApiIntegrationTestUtil.addNewBook(header, publisherResponseEntity.getBody().getPublisherId());
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Book responseBook = response.getBody();
        Assert.assertNotNull(responseBook);

        // Now we get the Book with and BookID that doesnot exist
        URI getBookUri = null;
        try {
            getBookUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.BOOK_API_BASE_URL + "/12345");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ResponseEntity<Book> libBookResponse = testRestTemplate.exchange(getBookUri, HttpMethod.GET,
                new HttpEntity<Object>(header), Book.class);

        Assert.assertEquals(HttpStatus.NOT_FOUND, libBookResponse.getStatusCode());
    }


    @Test
    public void updateBook_success() {

        String port = environment.getProperty("local.server.port");

        // Login with the admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        MultiValueMap<String, String> header = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));

        // First we need to add a Publisher because we need to add a book
        ResponseEntity<Publisher> publisherResponseEntity = libraryApiIntegrationTestUtil.addNewPublisher(header);

        // Now add a book
        ResponseEntity<Book> response = libraryApiIntegrationTestUtil.addNewBook(header, publisherResponseEntity.getBody().getPublisherId());
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Book responseBook = response.getBody();
        Assert.assertNotNull(responseBook);

        // Add another Publisher because we need to update the book with this new publisher
        ResponseEntity<Publisher> secondPublisherResponseEntity = libraryApiIntegrationTestUtil.addNewPublisher(header);

        // Set the values to be updated
        responseBook.setEdition("Second Edition");
        responseBook.setYearPublished(2012);
        responseBook.setPublisherId(secondPublisherResponseEntity.getBody().getPublisherId());

        URI bookUri = null;
        try {
            bookUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.BOOK_API_BASE_URL + "/" + responseBook.getBookId());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        HttpEntity<Book> request = new HttpEntity<>(responseBook, header);
        ResponseEntity<Book> libBookResponse = testRestTemplate.exchange(
                bookUri, HttpMethod.PUT, request,
                Book.class);
        // Check if update was successful
        Assert.assertEquals(HttpStatus.OK, libBookResponse.getStatusCode());

        // Get the updated Book
       libBookResponse = testRestTemplate.exchange(
               bookUri, HttpMethod.GET, new HttpEntity<Object>(header),
                Book.class);

        Assert.assertEquals(HttpStatus.OK, libBookResponse.getStatusCode());
        Book book = libBookResponse.getBody();

        // Check if the response has updated details
        Assert.assertEquals("Second Edition", book.getEdition());
        Assert.assertEquals(2012, book.getYearPublished().longValue());
        Assert.assertEquals(secondPublisherResponseEntity.getBody().getPublisherId(), book.getPublisherId());
    }

    @Test
    public void updateBook_failure_update_wrong_book() {

        String port = environment.getProperty("local.server.port");

        // Login with the admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        MultiValueMap<String, String> header = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));

        // First we need to add a Publisher because we need to add a book
        ResponseEntity<Publisher> publisherResponseEntity = libraryApiIntegrationTestUtil.addNewPublisher(header);

        // Now add a book
        ResponseEntity<Book> response = libraryApiIntegrationTestUtil.addNewBook(header, publisherResponseEntity.getBody().getPublisherId());
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Book responseBook = response.getBody();
        Assert.assertNotNull(responseBook);

        // Add another Publisher because we need to update the book with this new publisher
        ResponseEntity<Publisher> secondPublisherResponseEntity = libraryApiIntegrationTestUtil.addNewPublisher(header);

        // Set the values to be updated
        responseBook.setEdition("Second Edition");
        responseBook.setYearPublished(2012);
        responseBook.setPublisherId(secondPublisherResponseEntity.getBody().getPublisherId());

        URI bookUri = null;
        try {
            bookUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.BOOK_API_BASE_URL + "/1234");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        HttpEntity<Book> request = new HttpEntity<>(responseBook, header);
        ResponseEntity<Book> libBookResponse = testRestTemplate.exchange(
                bookUri, HttpMethod.PUT, request,
                Book.class);
        // Check if update was successful
        Assert.assertEquals(HttpStatus.BAD_REQUEST, libBookResponse.getStatusCode());

    }

    @Test
    public void updateBook_normal_user_unauthorized() {

        String port = environment.getProperty("local.server.port");

        // Login with the admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        MultiValueMap<String, String> header = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));

        // First we need to add a Publisher because we need to add a book
        ResponseEntity<Publisher> publisherResponseEntity = libraryApiIntegrationTestUtil.addNewPublisher(header);

        // Now add a book
        ResponseEntity<Book> response = libraryApiIntegrationTestUtil.addNewBook(header, publisherResponseEntity.getBody().getPublisherId());
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Book responseBook = response.getBody();
        Assert.assertNotNull(responseBook);

        // Add another Publisher because we need to update the book with this new publisher
        ResponseEntity<Publisher> secondPublisherResponseEntity = libraryApiIntegrationTestUtil.addNewPublisher(header);

        // Set the values to be updated
        responseBook.setEdition("Second Edition");
        responseBook.setYearPublished(2012);
        responseBook.setPublisherId(secondPublisherResponseEntity.getBody().getPublisherId());

        // Create a normal user
        ResponseEntity<LibraryUser> responseEntity = libraryApiIntegrationTestUtil.registerNewUser("update.book.normal.user.unauthorized");
        LibraryUser libraryUser = responseEntity.getBody();

        // Login with normal user credentials
        ResponseEntity<String> userLoginResponse = libraryApiIntegrationTestUtil.loginUser(libraryUser.getUsername(), libraryUser.getPassword());
        header = createAuthorizationHeader(userLoginResponse.getHeaders().get("Authorization").get(0));

        URI bookUri = null;
        try {
            bookUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.BOOK_API_BASE_URL + "/" + responseBook.getBookId());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // Send update request with normal User credential
        HttpEntity<Book> request = new HttpEntity<>(responseBook, header);
        ResponseEntity<Book> libBookResponse = testRestTemplate.exchange(
                bookUri, HttpMethod.PUT, request,
                Book.class);
        // Check if update was successful
        Assert.assertEquals(HttpStatus.FORBIDDEN, libBookResponse.getStatusCode());
    }

    @Test
    public void deleteBook_success() {

        String port = environment.getProperty("local.server.port");

        // Login with the admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        MultiValueMap<String, String> header = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));

        // First we need to add a Publisher because we need to add a book
        ResponseEntity<Publisher> publisherResponseEntity = libraryApiIntegrationTestUtil.addNewPublisher(header);

        // Now add a book
        ResponseEntity<Book> response = libraryApiIntegrationTestUtil.addNewBook(header, publisherResponseEntity.getBody().getPublisherId());
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Book responseBook = response.getBody();
        Assert.assertNotNull(responseBook);

        URI bookUri = null;
        try {
            bookUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.BOOK_API_BASE_URL + "/" + response.getBody().getBookId());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        HttpEntity<Book> request = new HttpEntity<>(responseBook, header);
        ResponseEntity<String> deleteResponse = testRestTemplate.exchange(
                bookUri, HttpMethod.DELETE, request,
                String.class);
        // Check if update was successful
        Assert.assertEquals(HttpStatus.ACCEPTED, deleteResponse.getStatusCode());

        // Get the updated Book
        ResponseEntity<Book> getResponse = testRestTemplate.exchange(
                bookUri, HttpMethod.GET, new HttpEntity<Object>(header),
                Book.class);

        // Book should not exist
        Assert.assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());

    }


    @Test
    public void deleteBook_failure_author_not_found() {

        String port = environment.getProperty("local.server.port");

        // Login with the admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        MultiValueMap<String, String> header = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));

        // First we need to add a Publisher because we need to add a book
        ResponseEntity<Publisher> publisherResponseEntity = libraryApiIntegrationTestUtil.addNewPublisher(header);

        // Now add a book
        ResponseEntity<Book> response = libraryApiIntegrationTestUtil.addNewBook(header, publisherResponseEntity.getBody().getPublisherId());
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Book responseBook = response.getBody();
        Assert.assertNotNull(responseBook);

        URI bookUri = null;
        try {
            bookUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.BOOK_API_BASE_URL + "/1234");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        HttpEntity<Book> request = new HttpEntity<>(responseBook, header);
        ResponseEntity<String> deleteResponse = testRestTemplate.exchange(
                bookUri, HttpMethod.DELETE, request,
                String.class);
        // Check if update was successful
        Assert.assertEquals(HttpStatus.NOT_FOUND, deleteResponse.getStatusCode());

    }


    @Test
    public void deleteBook_normal_user_unauthorized() {

        String port = environment.getProperty("local.server.port");

        // Login with the admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        MultiValueMap<String, String> header = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));

        // First we need to add a Publisher because we need to add a book
        ResponseEntity<Publisher> publisherResponseEntity = libraryApiIntegrationTestUtil.addNewPublisher(header);

        // Now add a book
        ResponseEntity<Book> response = libraryApiIntegrationTestUtil.addNewBook(header, publisherResponseEntity.getBody().getPublisherId());
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Book responseBook = response.getBody();
        Assert.assertNotNull(responseBook);

        // Create a normal user
        ResponseEntity<LibraryUser> responseEntity = libraryApiIntegrationTestUtil.registerNewUser("delete.book.normal.user.unauthorized");
        LibraryUser libraryUser = responseEntity.getBody();

        // Login with normal user credentials
        ResponseEntity<String> userLoginResponse = libraryApiIntegrationTestUtil.loginUser(libraryUser.getUsername(), libraryUser.getPassword());
        header = createAuthorizationHeader(userLoginResponse.getHeaders().get("Authorization").get(0));

        URI bookUri = null;
        try {
            bookUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.BOOK_API_BASE_URL + "/" + responseBook.getBookId());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // Send update request with normal User credential
        HttpEntity<Book> request = new HttpEntity<>(responseBook, header);
        ResponseEntity<Book> libBookResponse = testRestTemplate.exchange(
                bookUri, HttpMethod.DELETE, request,
                Book.class);
        // Check if update was successful
        Assert.assertEquals(HttpStatus.FORBIDDEN, libBookResponse.getStatusCode());
    }

    @Test
    public void addBookAuhors_success() {

        String port = environment.getProperty("local.server.port");

        // Login with the admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        MultiValueMap<String, String> header = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));

        // First we need to add a Publisher because we need to add a book
        ResponseEntity<Publisher> publisherResponseEntity = libraryApiIntegrationTestUtil.addNewPublisher(header);

        // Now add a book
        ResponseEntity<Book> response = libraryApiIntegrationTestUtil.addNewBook(header, publisherResponseEntity.getBody().getPublisherId());
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Book responseBook = response.getBody();
        Assert.assertNotNull(responseBook);

        // Response should not have any Authors
        Assert.assertEquals(0, responseBook.getAuthors().size());

        // Add an Author
        ResponseEntity<Author> authorResponseEntity = libraryApiIntegrationTestUtil.addNewAuthor(header);

        String bookUrl = TestConstants.API_BASE_URL + port + TestConstants.BOOK_API_BASE_URL + "/" + responseBook.getBookId() ;

        URI bookAddAuthorUri = null;
        try {
            bookAddAuthorUri = new URI(bookUrl + TestConstants.BOOK_ADD_AUTHOR_API_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Integer[] authorsOfTheBook = new Integer[]{authorResponseEntity.getBody().getAuthorId()};
        HttpEntity<Integer[]> request = new HttpEntity<>(authorsOfTheBook, header);
        ResponseEntity<Book> libBookResponse = testRestTemplate.exchange(
                bookAddAuthorUri, HttpMethod.PUT, request,
                Book.class);
        // Check if update was successful
        Assert.assertEquals(HttpStatus.OK, libBookResponse.getStatusCode());

        URI bookUri = null;
        try {
            bookUri = new URI(bookUrl);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // Get the updated Book
        libBookResponse = testRestTemplate.exchange(
                bookUri, HttpMethod.GET, new HttpEntity<Object>(header),
                Book.class);

        Assert.assertEquals(HttpStatus.OK, libBookResponse.getStatusCode());
        Book book = libBookResponse.getBody();

        // Check if the response has Author details
        Assert.assertEquals(1, book.getAuthors().size());
        Assert.assertNotNull(book.getAuthors().stream()
                .filter(author -> author.getLastName().contains(TestConstants.TEST_AUTHOR_LAST_NAME)));
    }

    @Test
    public void addBookAuhors_normal_user_unauthorized() {

        String port = environment.getProperty("local.server.port");

        // Login with the admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        MultiValueMap<String, String> header = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));

        // First we need to add a Publisher because we need to add a book
        ResponseEntity<Publisher> publisherResponseEntity = libraryApiIntegrationTestUtil.addNewPublisher(header);

        // Now add a book
        ResponseEntity<Book> response = libraryApiIntegrationTestUtil.addNewBook(header, publisherResponseEntity.getBody().getPublisherId());
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Book responseBook = response.getBody();
        Assert.assertNotNull(responseBook);

        // Create a normal user
        ResponseEntity<LibraryUser> responseEntity = libraryApiIntegrationTestUtil.registerNewUser("add.book.authors.normal.user.unauthorized");
        LibraryUser libraryUser = responseEntity.getBody();

        // Login with normal user credentials
        ResponseEntity<String> userLoginResponse = libraryApiIntegrationTestUtil.loginUser(libraryUser.getUsername(), libraryUser.getPassword());
        header = createAuthorizationHeader(userLoginResponse.getHeaders().get("Authorization").get(0));

        // Response should not have any Authors
        Assert.assertEquals(0, responseBook.getAuthors().size());

        // Add an Author
        ResponseEntity<Author> authorResponseEntity = libraryApiIntegrationTestUtil.addNewAuthor(header);

        String bookUrl = TestConstants.API_BASE_URL + port + TestConstants.BOOK_API_BASE_URL + "/" + responseBook.getBookId() ;

        URI bookAddAuthorUri = null;
        try {
            bookAddAuthorUri = new URI(bookUrl + TestConstants.BOOK_ADD_AUTHOR_API_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Integer[] authorsOfTheBook = new Integer[]{authorResponseEntity.getBody().getAuthorId()};
        HttpEntity<Integer[]> request = new HttpEntity<>(authorsOfTheBook, header);
        ResponseEntity<Book> libBookResponse = testRestTemplate.exchange(
                bookAddAuthorUri, HttpMethod.PUT, request,
                Book.class);
        // Check if update was successful
        Assert.assertEquals(HttpStatus.FORBIDDEN, libBookResponse.getStatusCode());
    }

    @Test
    public void addBookAuhors_author_doesnot_exist() {

        String port = environment.getProperty("local.server.port");

        // Login with the admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        MultiValueMap<String, String> header = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));

        // First we need to add a Publisher because we need to add a book
        ResponseEntity<Publisher> publisherResponseEntity = libraryApiIntegrationTestUtil.addNewPublisher(header);

        // Now add a book
        ResponseEntity<Book> response = libraryApiIntegrationTestUtil.addNewBook(header, publisherResponseEntity.getBody().getPublisherId());
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Book responseBook = response.getBody();
        Assert.assertNotNull(responseBook);

        // Response should not have any Authors
        Assert.assertEquals(0, responseBook.getAuthors().size());

        // Add an Author
        ResponseEntity<Author> authorResponseEntity = libraryApiIntegrationTestUtil.addNewAuthor(header);

        String bookUrl = TestConstants.API_BASE_URL + port + TestConstants.BOOK_API_BASE_URL + "/" + responseBook.getBookId() ;

        URI bookAddAuthorUri = null;
        try {
            bookAddAuthorUri = new URI(bookUrl + TestConstants.BOOK_ADD_AUTHOR_API_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Integer[] authorsOfTheBook = new Integer[]{1234, 5678};
        HttpEntity<Integer[]> request = new HttpEntity<>(authorsOfTheBook, header);
        ResponseEntity<Book> libBookResponse = testRestTemplate.exchange(
                bookAddAuthorUri, HttpMethod.PUT, request,
                Book.class);
        // Check if update was successful
        Assert.assertEquals(HttpStatus.NOT_FOUND, libBookResponse.getStatusCode());
    }


    @Test
    public void searchBookByIsbn_success() {

        String port = environment.getProperty("local.server.port");

        // Login with the admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        MultiValueMap<String, String> header = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));

        // We need to add a Publisher because we need to add a book
        ResponseEntity<Publisher> publisherResponseEntity = libraryApiIntegrationTestUtil.addNewPublisher(header);
        Publisher publisher = publisherResponseEntity.getBody();

        // Add a Books
        for(int i=0; i<5; i++) {
            libraryApiIntegrationTestUtil.addNewBook(header, publisher.getPublisherId());
        }

        URI bookSearchUri = null;
        try {
            bookSearchUri = new URI(TestConstants.BOOK_API_GET_BY_ISBN_URL + TestConstants.TEST_BOOK_ISBN + 1);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ResponseEntity<Book> response = testRestTemplate.exchange(bookSearchUri, HttpMethod.GET,
                new HttpEntity<Object>(header), Book.class);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());

        Book book = response.getBody();
        Assert.assertNotNull(book);
        Assert.assertTrue(book.getTitle().contains(TestConstants.TEST_BOOK_TITLE));
        Assert.assertEquals(TestConstants.TEST_BOOK_ISBN + 1, book.getIsbn());

    }

    @Test
    public void searchBookByIsbn_book_doesnot_exist() {

        String port = environment.getProperty("local.server.port");

        // Login with the admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        MultiValueMap<String, String> header = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));

        // We need to add a Publisher because we need to add a book
        ResponseEntity<Publisher> publisherResponseEntity = libraryApiIntegrationTestUtil.addNewPublisher(header);
        Publisher publisher = publisherResponseEntity.getBody();

        // Add a Books
        libraryApiIntegrationTestUtil.addNewBook(header, publisher.getPublisherId());

        URI bookSearchUri = null;
        try {
            bookSearchUri = new URI(TestConstants.BOOK_API_GET_BY_ISBN_URL + 1234);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ResponseEntity<Book> response = testRestTemplate.exchange(bookSearchUri, HttpMethod.GET,
                new HttpEntity<Object>(header), Book.class);
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    }

    @Test
    public void searchBookByTitle_success() {

        String port = environment.getProperty("local.server.port");

        // Login with the admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        MultiValueMap<String, String> header = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));

        // We need to add a Publisher because we need to add a book
        ResponseEntity<Publisher> publisherResponseEntity = libraryApiIntegrationTestUtil.addNewPublisher(header);
        Publisher publisher = publisherResponseEntity.getBody();

        // Add few Books
        for(int i=0; i<5; i++) {
            libraryApiIntegrationTestUtil.addNewBook(header, publisher.getPublisherId());
        }

        URI bookSearchUri = null;
        try {
            bookSearchUri = new URI(TestConstants.BOOK_API_SEARCH_URL +
                    "?title=" + "SpringBoot");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ResponseEntity<Book[]> response = testRestTemplate.exchange(bookSearchUri, HttpMethod.GET,
                new HttpEntity<Object>(header), Book[].class);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertTrue(response.getBody().length > 5);
    }


    private MultiValueMap<String, String> createAuthorizationHeader(String bearerToken) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", bearerToken);

        return headers;
    }

}