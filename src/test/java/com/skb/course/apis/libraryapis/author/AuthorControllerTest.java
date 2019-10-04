package com.skb.course.apis.libraryapis.author;

import com.skb.course.apis.libraryapis.testutils.TestConstants;
import com.skb.course.apis.libraryapis.testutils.LibraryApiIntegrationTestUtil;
import com.skb.course.apis.libraryapis.model.common.Gender;
import com.skb.course.apis.libraryapis.user.LibraryUser;
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
import java.time.LocalDate;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthorControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private LibraryApiIntegrationTestUtil libraryApiIntegrationTestUtil;

    @Value("${library.api.user.admin.username}")
    private String adminUsername;

    @Value("${library.api.user.admin.password}")
    private String adminPassword;

    private static int authorCtr;

    @Before
    public void setUp() throws Exception {
    }

    @Autowired
    Environment environment;

    @Test
    public void test_admin_login_successful() {

        // Login with admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);

        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        String authToken = loginResponse.getHeaders().get("Authorization").get(0);
        Assert.assertNotNull(authToken);
        Assert.assertTrue(authToken.length() > 0);
    }

    @Test
    public void test_admin_login_unsuccessful() {

        // Login with wrong credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser("blahblah", "blahblah");

        Assert.assertEquals(HttpStatus.FORBIDDEN, loginResponse.getStatusCode());
        Assert.assertNull(loginResponse.getHeaders().get("Authorization"));
    }

    @Test
    public void addAuthor_success() {

        // Login with the admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        ResponseEntity<Author> response = libraryApiIntegrationTestUtil.addNewAuthor(
                createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0)));
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Author responseAuthor = response.getBody();
        Assert.assertNotNull(responseAuthor);
        Assert.assertNotNull(responseAuthor.getAuthorId());
        Assert.assertTrue(responseAuthor.getFirstName().contains(TestConstants.TEST_AUTHOR_FIRST_NAME));
        Assert.assertTrue(responseAuthor.getLastName().contains(TestConstants.TEST_AUTHOR_LAST_NAME));

    }

    @Test
    public void getAuthor_success_admin_user() {

        String port = environment.getProperty("local.server.port");

        // Login with the admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        MultiValueMap<String, String> header = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));
        // Add a Author
        ResponseEntity<Author> response = libraryApiIntegrationTestUtil.addNewAuthor(header);
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Now we get the Author
        URI getAuthorUri = null;
        try {
            getAuthorUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.AUTHOR_API_BASE_URL + "/" + response.getBody().getAuthorId());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ResponseEntity<Author> libAuthorResponse = testRestTemplate.exchange(getAuthorUri, HttpMethod.GET,
                new HttpEntity<Object>(header), Author.class);

        Assert.assertEquals(HttpStatus.OK, libAuthorResponse.getStatusCode());
        Author responseAuthor = libAuthorResponse.getBody();
        Assert.assertNotNull(responseAuthor.getAuthorId());
        Assert.assertTrue(responseAuthor.getFirstName().contains(TestConstants.TEST_AUTHOR_FIRST_NAME));
        Assert.assertTrue(responseAuthor.getLastName().contains(TestConstants.TEST_AUTHOR_LAST_NAME));
    }

    @Test
    public void getAuthor_success_normal_user() {

        String port = environment.getProperty("local.server.port");

        // Login with the admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        MultiValueMap<String, String> header = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));
        // Add a Author
        ResponseEntity<Author> response = libraryApiIntegrationTestUtil.addNewAuthor(header);
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Create a normal user
        ResponseEntity<LibraryUser> responseEntity = libraryApiIntegrationTestUtil.registerNewUser("get.author.success.normal.user");
        LibraryUser libraryUser = responseEntity.getBody();

        // Login with normal user credentials
        ResponseEntity<String> userLoginResponse = libraryApiIntegrationTestUtil.loginUser(libraryUser.getUsername(), libraryUser.getPassword());
        header = createAuthorizationHeader(userLoginResponse.getHeaders().get("Authorization").get(0));

        URI getAuthorUri = null;
        try {
            getAuthorUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.AUTHOR_API_BASE_URL + "/" + response.getBody().getAuthorId());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ResponseEntity<Author> libAuthorResponse = testRestTemplate.exchange(getAuthorUri, HttpMethod.GET,
                new HttpEntity<Object>(header), Author.class);

        Assert.assertEquals(HttpStatus.OK, libAuthorResponse.getStatusCode());
        Author responseAuthor = libAuthorResponse.getBody();
        Assert.assertNotNull(responseAuthor.getAuthorId());
        Assert.assertTrue(responseAuthor.getFirstName().contains(TestConstants.TEST_AUTHOR_FIRST_NAME));
        Assert.assertTrue(responseAuthor.getLastName().contains(TestConstants.TEST_AUTHOR_LAST_NAME));
    }

    @Test
    public void getAuthor_author_not_found() {

        String port = environment.getProperty("local.server.port");

        // Login with the admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        MultiValueMap<String, String> header = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));
        // Add a Author
        ResponseEntity<Author> response = libraryApiIntegrationTestUtil.addNewAuthor(header);
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Now we get the Author with and AuthorID that doesnot exist
        URI getAuthorUri = null;
        try {
            getAuthorUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.AUTHOR_API_BASE_URL + "/12345");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ResponseEntity<Author> libAuthorResponse = testRestTemplate.exchange(getAuthorUri, HttpMethod.GET,
                new HttpEntity<Object>(header), Author.class);

        Assert.assertEquals(HttpStatus.NOT_FOUND, libAuthorResponse.getStatusCode());
    }


    @Test
    public void updateAuthor_success() {

        String port = environment.getProperty("local.server.port");

        // Login with the admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        MultiValueMap<String, String> header = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));
        // Add a Author
        ResponseEntity<Author> response = libraryApiIntegrationTestUtil.addNewAuthor(header);
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Author responseAuthor = response.getBody();
        Assert.assertNotNull(responseAuthor);

        // Set the values to be updated
        responseAuthor.setGender(Gender.Female);
        LocalDate updatedDob = responseAuthor.getDateOfBirth().minusMonths(2);
        responseAuthor.setDateOfBirth(updatedDob);

        URI authorUri = null;
        try {
            authorUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.AUTHOR_API_BASE_URL + "/" + response.getBody().getAuthorId());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        HttpEntity<Author> request = new HttpEntity<>(responseAuthor, header);
        ResponseEntity<Author> libAuthorResponse = testRestTemplate.exchange(
                authorUri, HttpMethod.PUT, request,
                Author.class);
        // Check if update was successful
        Assert.assertEquals(HttpStatus.OK, libAuthorResponse.getStatusCode());

        // Get the updated Author
       libAuthorResponse = testRestTemplate.exchange(
               authorUri, HttpMethod.GET, new HttpEntity<Object>(header),
                Author.class);

        Assert.assertEquals(HttpStatus.OK, libAuthorResponse.getStatusCode());
        Author author = libAuthorResponse.getBody();

        // Check if the response has updated details
        Assert.assertEquals(Gender.Female, author.getGender());
        Assert.assertEquals(updatedDob, author.getDateOfBirth());
    }

    @Test
    public void updateAuthor_failure_update_wrong_author() {

        String port = environment.getProperty("local.server.port");

        // Login with the admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        MultiValueMap<String, String> header = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));
        // Add a Author
        ResponseEntity<Author> response = libraryApiIntegrationTestUtil.addNewAuthor(header);
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Author responseAuthor = response.getBody();
        Assert.assertNotNull(responseAuthor);

        // Set the values to be updated
        responseAuthor.setGender(Gender.Female);
        LocalDate updatedDob = responseAuthor.getDateOfBirth().minusMonths(2);
        responseAuthor.setDateOfBirth(updatedDob);

        URI authorUri = null;
        try {
            authorUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.AUTHOR_API_BASE_URL + "/1234");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        HttpEntity<Author> request = new HttpEntity<>(responseAuthor, header);
        ResponseEntity<Author> libAuthorResponse = testRestTemplate.exchange(
                authorUri, HttpMethod.PUT, request,
                Author.class);
        // Check if update was successful
        Assert.assertEquals(HttpStatus.BAD_REQUEST, libAuthorResponse.getStatusCode());

    }

    @Test
    public void updateAuthor_normal_user_unauthorized() {

        String port = environment.getProperty("local.server.port");

        // Login with the admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        MultiValueMap<String, String> header = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));
        // Add a Author
        ResponseEntity<Author> response = libraryApiIntegrationTestUtil.addNewAuthor(header);
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Update the Author. Set the values to be updated
        Author responseAuthor = response.getBody();
        responseAuthor.setGender(Gender.Female);
        LocalDate updatedDob = responseAuthor.getDateOfBirth().minusMonths(2);
        responseAuthor.setDateOfBirth(updatedDob);

        // Create a normal user
        ResponseEntity<LibraryUser> responseEntity = libraryApiIntegrationTestUtil.registerNewUser("update.author.normal.user.unauthorized");
        LibraryUser libraryUser = responseEntity.getBody();

        // Login with normal user credentials
        ResponseEntity<String> userLoginResponse = libraryApiIntegrationTestUtil.loginUser(libraryUser.getUsername(), libraryUser.getPassword());
        header = createAuthorizationHeader(userLoginResponse.getHeaders().get("Authorization").get(0));

        URI authorUri = null;
        try {
            authorUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.AUTHOR_API_BASE_URL + "/" + responseAuthor.getAuthorId());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // Send update request with normal User credential
        HttpEntity<Author> request = new HttpEntity<>(responseAuthor, header);
        ResponseEntity<Author> libAuthorResponse = testRestTemplate.exchange(
                authorUri, HttpMethod.PUT, request,
                Author.class);
        // Check if update was successful
        Assert.assertEquals(HttpStatus.FORBIDDEN, libAuthorResponse.getStatusCode());
    }

    @Test
    public void deleteAuthor_success() {

        String port = environment.getProperty("local.server.port");

        // Login with the admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        MultiValueMap<String, String> header = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));
        // Add a Author
        ResponseEntity<Author> response = libraryApiIntegrationTestUtil.addNewAuthor(header);
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Author responseAuthor = response.getBody();
        Assert.assertNotNull(responseAuthor);

        URI authorUri = null;
        try {
            authorUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.AUTHOR_API_BASE_URL + "/" + response.getBody().getAuthorId());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        HttpEntity<Author> request = new HttpEntity<>(responseAuthor, header);
        ResponseEntity<String> deleteResponse = testRestTemplate.exchange(
                authorUri, HttpMethod.DELETE, request,
                String.class);
        // Check if update was successful
        Assert.assertEquals(HttpStatus.ACCEPTED, deleteResponse.getStatusCode());

        // Get the updated Author
        ResponseEntity<Author> getResponse = testRestTemplate.exchange(
                authorUri, HttpMethod.GET, new HttpEntity<Object>(header),
                Author.class);

        // Author should not exist
        Assert.assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());

    }

    @Test
    public void deleteAuthor_failure_author_not_found() {

        String port = environment.getProperty("local.server.port");

        // Login with the admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        MultiValueMap<String, String> header = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));
        // Add a Author
        ResponseEntity<Author> response = libraryApiIntegrationTestUtil.addNewAuthor(header);
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Author responseAuthor = response.getBody();
        Assert.assertNotNull(responseAuthor);

        URI authorUri = null;
        try {
            authorUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.AUTHOR_API_BASE_URL + "/1234");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        HttpEntity<Author> request = new HttpEntity<>(responseAuthor, header);
        ResponseEntity<String> deleteResponse = testRestTemplate.exchange(
                authorUri, HttpMethod.DELETE, request,
                String.class);
        // Check if update was successful
        Assert.assertEquals(HttpStatus.NOT_FOUND, deleteResponse.getStatusCode());

    }

    @Test
    public void deleteAuthor_normal_user_unauthorized() {

        String port = environment.getProperty("local.server.port");

        // Login with the admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        MultiValueMap<String, String> header = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));
        // Add a Author
        ResponseEntity<Author> response = libraryApiIntegrationTestUtil.addNewAuthor(header);
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Update the Author. Set the values to be updated
        Author responseAuthor = response.getBody();

        // Create a normal user
        ResponseEntity<LibraryUser> responseEntity = libraryApiIntegrationTestUtil.registerNewUser("delete.author.normal.user.unauthorized");
        LibraryUser libraryUser = responseEntity.getBody();

        // Login with normal user credentials
        ResponseEntity<String> userLoginResponse = libraryApiIntegrationTestUtil.loginUser(libraryUser.getUsername(), libraryUser.getPassword());
        header = createAuthorizationHeader(userLoginResponse.getHeaders().get("Authorization").get(0));

        URI authorUri = null;
        try {
            authorUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.AUTHOR_API_BASE_URL + "/" + responseAuthor.getAuthorId());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // Send update request with normal User credential
        HttpEntity<Author> request = new HttpEntity<>(responseAuthor, header);
        ResponseEntity<Author> libAuthorResponse = testRestTemplate.exchange(
                authorUri, HttpMethod.DELETE, request,
                Author.class);
        // Check if update was successful
        Assert.assertEquals(HttpStatus.FORBIDDEN, libAuthorResponse.getStatusCode());
    }

    @Test
    public void searchAuthor_success() {

        String port = environment.getProperty("local.server.port");

        // Login with the admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        MultiValueMap<String, String> header = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));
        // Add a Authors
        for(int i=0; i<10; i++) {
            libraryApiIntegrationTestUtil.addNewAuthor(header);
        }

        URI authorSearchUri = null;
        try {
            authorSearchUri = new URI(TestConstants.AUTHOR_API_SEARCH_URL +
                    "?firstName=&lastName=" + TestConstants.TEST_AUTHOR_LAST_NAME);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ResponseEntity<Author[]> response = testRestTemplate.exchange(authorSearchUri, HttpMethod.GET,
                new HttpEntity<Object>(header), Author[].class);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        // Comparing with >= 10 because depending upon which other test methods run the number of Authors may vary
        Assert.assertTrue(response.getBody().length >= 10);
        for(Author Author : response.getBody()) {
            Assert.assertTrue(Author.getFirstName().contains(TestConstants.TEST_AUTHOR_FIRST_NAME));
            Assert.assertTrue(Author.getLastName().contains(TestConstants.TEST_AUTHOR_LAST_NAME));
        }

    }

    @Test
    public void searchAuthors_no_Authors() {

        // Login with the admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        MultiValueMap<String, String> header = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));
        // Add a Authors
        for(int i=0; i<10; i++) {
            libraryApiIntegrationTestUtil.addNewAuthor(header);
        }

        URI authorSearchUri = null;
        try {
            authorSearchUri = new URI(TestConstants.AUTHOR_API_SEARCH_URL +
                    "?firstName=&lastName=BlahBlah");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ResponseEntity<String> response = testRestTemplate.exchange(authorSearchUri, HttpMethod.GET,
                new HttpEntity<Object>(header), String.class);

        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    private MultiValueMap<String, String> createAuthorizationHeader(String bearerToken) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", bearerToken);

        return headers;
    }

}