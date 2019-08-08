package com.skb.course.apis.libraryapis.controller;

import com.skb.course.apis.libraryapis.TestConstants;
import com.skb.course.apis.libraryapis.model.*;
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
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private LibraryApiIntegrationTestUtil libraryApiIntegrationTestUtil;

    private static int userCtr;

    @Value("${library.api.user.admin.username}")
    private String adminUsername;

    @Value("${library.api.user.admin.password}")
    private String adminPassword;

    @Before
    public void setUp() throws Exception {
    }

    @Autowired
    Environment environment;

    @Test
    public void test_login_successful() {

        // First we register a user
        ResponseEntity<LibraryUser> registerUserResponse = libraryApiIntegrationTestUtil.registerNewUser("test.login.successful");

        Assert.assertEquals(HttpStatus.CREATED, registerUserResponse.getStatusCode());

        LibraryUser responseLibraryUser = registerUserResponse.getBody();
        Assert.assertNotNull(responseLibraryUser);

        // Login with supplied username and default password
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(responseLibraryUser.getUsername(), responseLibraryUser.getPassword());

        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        String authToken = loginResponse.getHeaders().get("Authorization").get(0);
        Assert.assertNotNull(authToken);
        Assert.assertTrue(authToken.length() > 0);
    }

    @Test
    public void test_login_unsuccessful() {

        // Login with wrong credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser("blahblah", "blahblah");

        Assert.assertEquals(HttpStatus.FORBIDDEN, loginResponse.getStatusCode());
        Assert.assertNull(loginResponse.getHeaders().get("Authorization"));
    }

    @Test
    public void registerUser_success() {

        ResponseEntity<LibraryUser> response = libraryApiIntegrationTestUtil.registerNewUser("register.user.success");

        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        LibraryUser responseLibraryUser = response.getBody();
        Assert.assertNotNull(responseLibraryUser);
        Assert.assertNotNull(responseLibraryUser.getUserId());
        Assert.assertNotNull(responseLibraryUser.getPassword());
        Assert.assertTrue(responseLibraryUser.getUsername().equals("register.user.success"));
        Assert.assertTrue(responseLibraryUser.getFirstName().contains(TestConstants.TEST_USER_FIRST_NAME));
        Assert.assertTrue(responseLibraryUser.getLastName().contains(TestConstants.TEST_USER_LAST_NAME));
        Assert.assertNull(responseLibraryUser.getRole());

    }

    @Test
    public void getUser_success() {

        String port = environment.getProperty("local.server.port");

        // First we register a user
        ResponseEntity<LibraryUser> response = libraryApiIntegrationTestUtil.registerNewUser("get.user.success");

        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        LibraryUser responseLibraryUser = response.getBody();
        Assert.assertNotNull(responseLibraryUser);
        Integer userId = responseLibraryUser.getUserId();

        // Login with the credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(responseLibraryUser.getUsername(), responseLibraryUser.getPassword());

        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        // Now we get the registered user
        URI getUserUri = null;
        try {
            getUserUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.USER_API_BASE_URL + "/" + userId);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        MultiValueMap<String, String> headers = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));
        ResponseEntity<LibraryUser> libUserResponse = testRestTemplate.exchange(
                getUserUri, HttpMethod.GET, new HttpEntity<Object>(headers),
                LibraryUser.class);

        Assert.assertEquals(HttpStatus.OK, libUserResponse.getStatusCode());
        LibraryUser libraryUser = libUserResponse.getBody();
        Assert.assertNotNull(libraryUser.getUserId());
        Assert.assertTrue(libraryUser.getUsername().equals("get.user.success"));
        Assert.assertTrue(libraryUser.getFirstName().contains(TestConstants.TEST_USER_FIRST_NAME));
        Assert.assertTrue(libraryUser.getLastName().contains(TestConstants.TEST_USER_LAST_NAME));
        Assert.assertNull(libraryUser.getRole());
    }

    @Test
    public void getUser_unauthorized_for_different_user() {

        String port = environment.getProperty("local.server.port");

        // First we register a user
        ResponseEntity<LibraryUser> response = libraryApiIntegrationTestUtil.registerNewUser("getUser_unauthorized_for_different.user");
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        LibraryUser responseLibraryUser = response.getBody();
        Integer userId = responseLibraryUser.getUserId() + 1;

        // Login with the credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(responseLibraryUser.getUsername(), responseLibraryUser.getPassword());
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        // Now we get the registered user
        URI getUserUri = null;
        try {
            getUserUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.USER_API_BASE_URL + "/" + userId);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        MultiValueMap<String, String> headers = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));
        ResponseEntity<String> libUserResponse = testRestTemplate.exchange(
                getUserUri, HttpMethod.GET, new HttpEntity<Object>(headers),
                String.class);

        // Unauthorized to get another user details
        Assert.assertEquals(HttpStatus.FORBIDDEN, libUserResponse.getStatusCode());
    }

    @Test
    public void updateUser_success() {

        String port = environment.getProperty("local.server.port");

        // First we register a user
        ResponseEntity<LibraryUser> response = libraryApiIntegrationTestUtil.registerNewUser("update.user.success");

        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        LibraryUser responseLibraryUser = response.getBody();
        Assert.assertNotNull(responseLibraryUser);
        Integer userId = responseLibraryUser.getUserId();

        // Login with the credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(responseLibraryUser.getUsername(), responseLibraryUser.getPassword());

        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        // Set the values to be updated
        responseLibraryUser.setPassword("NewPassword");
        responseLibraryUser.setEmailId("newemailaddress@email.com");
        responseLibraryUser.setPhoneNumber(TestConstants.TEST_USER_PHONE_UPDATED);

        URI userUri = null;
        try {
            userUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.USER_API_BASE_URL + "/" + userId);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        MultiValueMap<String, String> headers = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));

        HttpEntity<LibraryUser> request = new HttpEntity<>(responseLibraryUser, headers);
        ResponseEntity<LibraryUser> libUserResponse = testRestTemplate.exchange(
                userUri, HttpMethod.PUT, request,
                LibraryUser.class);

        Assert.assertEquals(HttpStatus.OK, libUserResponse.getStatusCode());

        // Now login with changed password
        loginResponse = libraryApiIntegrationTestUtil.loginUser(responseLibraryUser.getUsername(), responseLibraryUser.getPassword());
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        // Now we get the user. Should have update email and phone number
        // Put the new Auth token
        headers.replace("Authorization", loginResponse.getHeaders().get("Authorization"));

       libUserResponse = testRestTemplate.exchange(
               userUri, HttpMethod.GET, new HttpEntity<Object>(headers),
                LibraryUser.class);

        Assert.assertEquals(HttpStatus.OK, libUserResponse.getStatusCode());
        LibraryUser libraryUser = libUserResponse.getBody();
        Assert.assertEquals("newemailaddress@email.com", libraryUser.getEmailId());
        Assert.assertEquals(TestConstants.TEST_USER_PHONE_UPDATED, libraryUser.getPhoneNumber());
    }

    @Test
    public void updateUser_unauthorized_for_different_user() {

        String port = environment.getProperty("local.server.port");

        // First we register a user
        ResponseEntity<LibraryUser> response = libraryApiIntegrationTestUtil.registerNewUser("update.user.unauthorized.for.different.user");
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        LibraryUser responseLibraryUser = response.getBody();
        Assert.assertNotNull(responseLibraryUser);
        // Update the details of another userId (userId + 1)
        Integer userId = responseLibraryUser.getUserId() + 1;

        // Login with the credentials to perform update
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(responseLibraryUser.getUsername(), responseLibraryUser.getPassword());
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        // Set the values to be updated
        responseLibraryUser.setPassword("NewPassword");
        responseLibraryUser.setEmailId("newemailaddress@email.com");
        responseLibraryUser.setPhoneNumber(TestConstants.TEST_USER_PHONE_UPDATED);

        URI userUri = null;
        try {
            userUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.USER_API_BASE_URL + "/" + userId);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        MultiValueMap<String, String> headers = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));
        HttpEntity<LibraryUser> request = new HttpEntity<>(responseLibraryUser, headers);

        // We need to use RestTemplate because we need to set the ErrorHandler becaue TestRestTemplate
        // throws HttpRetryException when there is a response body with response status 401
        RestTemplate rTemplate = new RestTemplate();
        rTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        rTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            public boolean hasError(ClientHttpResponse response) throws IOException {
                HttpStatus statusCode = response.getStatusCode();
                return statusCode.series() == HttpStatus.Series.SERVER_ERROR;
            }
        });

        ResponseEntity<String> libUserResponse = rTemplate.exchange(
                userUri, HttpMethod.PUT, request,
                String.class);

        // Update for another user ID should not be allowed
        Assert.assertEquals(HttpStatus.FORBIDDEN, libUserResponse.getStatusCode());
    }

    @Test
    public void deleteUser_success() {

        String port = environment.getProperty("local.server.port");

        // First we register a user
        ResponseEntity<LibraryUser> response = libraryApiIntegrationTestUtil.registerNewUser("delete.user.success");
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        LibraryUser responseLibraryUser = response.getBody();
        Assert.assertNotNull(responseLibraryUser);
        Integer userId = responseLibraryUser.getUserId();

        // Login with the credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(responseLibraryUser.getUsername(), responseLibraryUser.getPassword());
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        // Delete the user
        URI userUri = null;
        try {
            userUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.USER_API_BASE_URL + "/" + userId);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        MultiValueMap<String, String> headers = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));

        HttpEntity<LibraryUser> request = new HttpEntity<>(responseLibraryUser, headers);
        ResponseEntity<String> libUserResponse = testRestTemplate.exchange(
                userUri, HttpMethod.DELETE, request, String.class);

        Assert.assertEquals(HttpStatus.ACCEPTED, libUserResponse.getStatusCode());

        // Now login again. You should not be able to login
        loginResponse = libraryApiIntegrationTestUtil.loginUser(responseLibraryUser.getUsername(), responseLibraryUser.getPassword());
        Assert.assertEquals(HttpStatus.FORBIDDEN, loginResponse.getStatusCode());

    }

    @Test
    public void deleteUser_unauthorized_for_different_user() {

        String port = environment.getProperty("local.server.port");

        // First we register a user
        ResponseEntity<LibraryUser> response = libraryApiIntegrationTestUtil.registerNewUser("delete.user.unauthorized.for.different.user");
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        LibraryUser responseLibraryUser = response.getBody();
        Assert.assertNotNull(responseLibraryUser);
        // Change the userId to something else
        Integer userId = responseLibraryUser.getUserId() + 1;

        // Login with the credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(responseLibraryUser.getUsername(), responseLibraryUser.getPassword());
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        // Delete the user
        URI userUri = null;
        try {
            userUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.USER_API_BASE_URL + "/" + userId);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        MultiValueMap<String, String> headers = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));

        HttpEntity<LibraryUser> request = new HttpEntity<>(responseLibraryUser, headers);
        ResponseEntity<String> libUserResponse = testRestTemplate.exchange(
                userUri, HttpMethod.DELETE, request, String.class);

        Assert.assertEquals(HttpStatus.FORBIDDEN, libUserResponse.getStatusCode());

    }

    @Test
    public void searchUsers_success() {

        // Register 10 users
        for(int i=0; i<10; i++) {
            libraryApiIntegrationTestUtil.registerNewUser("searchUsers.success" + i);
        }

        URI searchUri = null;
        try {
            searchUri = new URI(TestConstants.USER_API_SEARCH_URL + "?firstName=" + TestConstants.TEST_USER_FIRST_NAME
            + "&lastName=" + TestConstants.TEST_USER_LAST_NAME);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ResponseEntity<LibraryUser[]> response = testRestTemplate.getForEntity(searchUri, LibraryUser[].class);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        // Comparing with >= 10 because depending upon which other test methods run the number of users may vary
        Assert.assertTrue(response.getBody().length >= 10);
        for(LibraryUser libraryUser : response.getBody()) {
            Assert.assertTrue(libraryUser.getFirstName().contains(TestConstants.TEST_USER_FIRST_NAME));
            Assert.assertTrue(libraryUser.getLastName().contains(TestConstants.TEST_USER_LAST_NAME));
        }
    }

    @Test
    public void issueBooks_success() {

        String port = environment.getProperty("local.server.port");

        // Login with the admin credentials
        ResponseEntity<String> adminLoginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, adminLoginResponse.getStatusCode());
        MultiValueMap<String, String> adminAuthHeader = createAuthorizationHeader(adminLoginResponse.getHeaders().get("Authorization").get(0));

        // First we need to add a Publisher because we need to add a book
        ResponseEntity<Publisher> publisherResponseEntity = libraryApiIntegrationTestUtil.addNewPublisher(adminAuthHeader);
        Publisher publisher = publisherResponseEntity.getBody();

        Set<Integer> bookIds = new HashSet<>(5);
        // Add few Books
        for(int i=0; i<5; i++) {
            bookIds.add(
                    libraryApiIntegrationTestUtil.addNewBook(adminAuthHeader, publisher.getPublisherId()).getBody().getBookId());
        }

        // Create a normal user
        ResponseEntity<LibraryUser> responseEntity = libraryApiIntegrationTestUtil.registerNewUser("issue.books.success");
        LibraryUser libraryUser = responseEntity.getBody();

        URI issueBooksUri = null;
        try {
            issueBooksUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.USER_API_BASE_URL + "/" +
                    libraryUser.getUserId() + TestConstants.USER_API_ISSUE_BOOK_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        HttpEntity<Set<Integer>> issueBooksRequest = new HttpEntity<>(bookIds, adminAuthHeader);
        ResponseEntity<IssueBookResponse> issueBookResponseEntity = testRestTemplate.exchange(issueBooksUri, HttpMethod.PUT,
                issueBooksRequest, IssueBookResponse.class);

        Assert.assertEquals(HttpStatus.OK, issueBookResponseEntity.getStatusCode());
        IssueBookResponse issueBookResponse = issueBookResponseEntity.getBody();
        Assert.assertEquals(bookIds.size(), issueBookResponse.getIssueBookStatuses().size());
    }

    @Test
    public void issueBooks_book_does_not_exist() {

        String port = environment.getProperty("local.server.port");

        // Login with the admin credentials
        ResponseEntity<String> adminLoginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, adminLoginResponse.getStatusCode());
        MultiValueMap<String, String> adminAuthHeader = createAuthorizationHeader(adminLoginResponse.getHeaders().get("Authorization").get(0));

        // First we need to add a Publisher because we need to add a book
        ResponseEntity<Publisher> publisherResponseEntity = libraryApiIntegrationTestUtil.addNewPublisher(adminAuthHeader);
        Publisher publisher = publisherResponseEntity.getBody();

        Set<Integer> bookIds = new HashSet<>(5);
        // Add few Books
        for(int i=0; i<5; i++) {
            bookIds.add(
                    libraryApiIntegrationTestUtil.addNewBook(adminAuthHeader, publisher.getPublisherId()).getBody().getBookId());
        }

        // These book Ids do not exist
        bookIds.add(999);
        bookIds.add(998);
        // Create a normal user
        ResponseEntity<LibraryUser> responseEntity = libraryApiIntegrationTestUtil.registerNewUser("issue.books.book.does.not.exist");
        LibraryUser libraryUser = responseEntity.getBody();

        URI issueBooksUri = null;
        try {
            issueBooksUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.USER_API_BASE_URL + "/" +
                    libraryUser.getUserId() + TestConstants.USER_API_ISSUE_BOOK_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        HttpEntity<Set<Integer>> issueBooksRequest = new HttpEntity<>(bookIds, adminAuthHeader);
        ResponseEntity<IssueBookResponse> issueBookResponseEntity = testRestTemplate.exchange(issueBooksUri, HttpMethod.PUT,
                issueBooksRequest, IssueBookResponse.class);

        Assert.assertEquals(HttpStatus.OK, issueBookResponseEntity.getStatusCode());
        IssueBookResponse issueBookResponse = issueBookResponseEntity.getBody();
        Collection<IssueBookStatus> issueBookStatuses = issueBookResponse.getIssueBookStatuses();
        Assert.assertEquals(bookIds.size(), issueBookStatuses.size());

        Assert.assertEquals(2, issueBookStatuses.stream()
                .filter(issueBookStatus -> issueBookStatus.getRemarks().equals("Book Not Found"))
                .count());
    }

    @Test
    public void issueBooks_no_copies_available() {

        String port = environment.getProperty("local.server.port");

        // Login with the admin credentials
        ResponseEntity<String> adminLoginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, adminLoginResponse.getStatusCode());
        MultiValueMap<String, String> adminAuthHeader = createAuthorizationHeader(adminLoginResponse.getHeaders().get("Authorization").get(0));

        // First we need to add a Publisher because we need to add a book
        ResponseEntity<Publisher> publisherResponseEntity = libraryApiIntegrationTestUtil.addNewPublisher(adminAuthHeader);
        Publisher publisher = publisherResponseEntity.getBody();

        // Add a Books
        Integer[] bookIds = new Integer[]{libraryApiIntegrationTestUtil.addNewBook(adminAuthHeader, publisher.getPublisherId()).getBody().getBookId()};

        // Create three normal user and issue books to them
        URI issueBooksUri = null;
        Set<Integer> userIds = new HashSet<>(3);
        for(int i=0;i<4;i++) {
            ResponseEntity<LibraryUser> responseEntity = libraryApiIntegrationTestUtil.registerNewUser("issue.books.no.copies.available.1");
            LibraryUser libraryUser = responseEntity.getBody();
            try {
                issueBooksUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.USER_API_BASE_URL + "/" +
                        libraryUser.getUserId() + TestConstants.USER_API_ISSUE_BOOK_URL);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            HttpEntity<Integer[]> issueBooksRequest = new HttpEntity<>(bookIds, adminAuthHeader);
            ResponseEntity<IssueBookResponse> issueBookResponseEntity = testRestTemplate.exchange(issueBooksUri, HttpMethod.PUT,
                    issueBooksRequest, IssueBookResponse.class);
            Assert.assertEquals(HttpStatus.OK, issueBookResponseEntity.getStatusCode());
            IssueBookResponse issueBookResponse = issueBookResponseEntity.getBody();
            Collection<IssueBookStatus> issueBookStatuses = issueBookResponse.getIssueBookStatuses();
            Assert.assertEquals(bookIds.length, issueBookStatuses.size());
            if(i < 3) {
                Assert.assertEquals(bookIds.length, issueBookStatuses.stream().
                        filter(issueBookStatus -> issueBookStatus.getRemarks().equals("Book Issued"))
                        .count());
            } else {
                Assert.assertEquals(1, issueBookStatuses.stream().
                        filter(issueBookStatus -> issueBookStatus.getRemarks().equals("No copies available"))
                        .count());
            }
        }

    }

    @Test
    public void returnBooks_success() {

        String port = environment.getProperty("local.server.port");

        // Login with the admin credentials
        ResponseEntity<String> adminLoginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, adminLoginResponse.getStatusCode());
        MultiValueMap<String, String> adminAuthHeader = createAuthorizationHeader(adminLoginResponse.getHeaders().get("Authorization").get(0));

        // First we need to add a Publisher because we need to add a book
        ResponseEntity<Publisher> publisherResponseEntity = libraryApiIntegrationTestUtil.addNewPublisher(adminAuthHeader);
        Publisher publisher = publisherResponseEntity.getBody();

        // Add a Book to issue
        int bookId = libraryApiIntegrationTestUtil.addNewBook(adminAuthHeader, publisher.getPublisherId()).getBody().getBookId();
        Integer[] bookIds = new Integer[]{bookId};

        // Create a normal user
        ResponseEntity<LibraryUser> responseEntity = libraryApiIntegrationTestUtil.registerNewUser("return.books.success");
        LibraryUser libraryUser = responseEntity.getBody();

        URI issueBooksUri = null;
        try {
            issueBooksUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.USER_API_BASE_URL + "/" +
                    libraryUser.getUserId() + TestConstants.USER_API_ISSUE_BOOK_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // Issue the book to the user
        HttpEntity<Integer[]> issueBooksRequest = new HttpEntity<>(bookIds, adminAuthHeader);
        ResponseEntity<IssueBookResponse> issueBookResponseEntity = testRestTemplate.exchange(issueBooksUri, HttpMethod.PUT,
                issueBooksRequest, IssueBookResponse.class);

        Assert.assertEquals(HttpStatus.OK, issueBookResponseEntity.getStatusCode());
        IssueBookResponse issueBookResponse = issueBookResponseEntity.getBody();
        Assert.assertEquals(bookIds.length, issueBookResponse.getIssueBookStatuses().size());

        // Return the book
        URI returnBooksUri = null;
        try {
            returnBooksUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.USER_API_BASE_URL + "/" +
                    libraryUser.getUserId() + TestConstants.USER_API_ISSUE_BOOK_URL + "/" + bookIds[0]);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        HttpEntity<LibraryUser> request = new HttpEntity<>(adminAuthHeader);
        ResponseEntity<String> returnBookResponse = testRestTemplate.exchange(
                returnBooksUri, HttpMethod.DELETE, request, String.class);

        Assert.assertEquals(HttpStatus.ACCEPTED, returnBookResponse.getStatusCode());
    }

    @Test
    public void searchUsers_no_users() {

        // Register 10 users
        for(int i=0; i<10; i++) {
            libraryApiIntegrationTestUtil.registerNewUser("searchUsers.no.users" + i);
        }

        URI searchUri = null;
        try {
            searchUri = new URI(TestConstants.USER_API_SEARCH_URL + "?firstName=BlahFn&lastName=BlahLn");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ResponseEntity<String> response = testRestTemplate.getForEntity(searchUri, String.class);
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    private MultiValueMap<String, String> createAuthorizationHeader(String bearerToken) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", bearerToken);

        return headers;
    }
}