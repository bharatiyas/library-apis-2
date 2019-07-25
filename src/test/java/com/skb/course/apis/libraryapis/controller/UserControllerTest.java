package com.skb.course.apis.libraryapis.controller;

import com.skb.course.apis.libraryapis.TestConstants;
import com.skb.course.apis.libraryapis.model.LibraryUser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private LibraryApiIntegrationTestUtil libraryApiIntegrationTestUtil;

    private static int userCtr;

    @Before
    public void setUp() throws Exception {
    }

    @Autowired
    Environment environment;

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

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", loginResponse.getHeaders().get("Authorization").get(0));
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

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", loginResponse.getHeaders().get("Authorization").get(0));
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
        responseLibraryUser.setPhoneNumber("23423523");

        URI userUri = null;
        try {
            userUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.USER_API_BASE_URL + "/" + userId);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", loginResponse.getHeaders().get("Authorization").get(0));

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
        Assert.assertEquals("23423523", libraryUser.getPhoneNumber());
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
        responseLibraryUser.setPhoneNumber("23423523");

        URI userUri = null;
        try {
            userUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.USER_API_BASE_URL + "/" + userId);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", loginResponse.getHeaders().get("Authorization").get(0));
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

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", loginResponse.getHeaders().get("Authorization").get(0));

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

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", loginResponse.getHeaders().get("Authorization").get(0));

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

}