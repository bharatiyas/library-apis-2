package com.skb.course.apis.libraryapis.controller;

import com.skb.course.apis.libraryapis.TestConstants;
import com.skb.course.apis.libraryapis.model.Gender;
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
import java.time.LocalDate;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {


    @Autowired
    private TestRestTemplate testRestTemplate;

    private static int userCtr;

    @Before
    public void setUp() throws Exception {
    }

    @Autowired
    Environment environment;

    @Test
    public void registerUser_success() {

        ResponseEntity<LibraryUser> response = registerNewUser();

        Assert.assertEquals(201, response.getStatusCodeValue());

        LibraryUser responseLibraryUser = response.getBody();
        Assert.assertNotNull(responseLibraryUser);
        Assert.assertNotNull(responseLibraryUser.getUserId());
        Assert.assertNotNull(responseLibraryUser.getPassword());
        Assert.assertEquals("test.user" + userCtr, responseLibraryUser.getUsername());
        Assert.assertEquals("TestFn", responseLibraryUser.getFirstName());
        Assert.assertEquals("TestLn", responseLibraryUser.getLastName());
        Assert.assertNull(responseLibraryUser.getRole());

    }

    @Test
    public void getUser_success() {

        String port = environment.getProperty("local.server.port");

        // First we register a user
        ResponseEntity<LibraryUser> response = registerNewUser();

        Assert.assertEquals(201, response.getStatusCodeValue());

        LibraryUser responseLibraryUser = response.getBody();
        Assert.assertNotNull(responseLibraryUser);
        Integer userId = responseLibraryUser.getUserId();

        // Login with the credentials
        ResponseEntity<String> loginResponse = loginUser(responseLibraryUser.getUsername(), responseLibraryUser.getPassword());

        Assert.assertEquals(200, loginResponse.getStatusCodeValue());

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

        Assert.assertEquals(200, libUserResponse.getStatusCodeValue());
        LibraryUser libraryUser = libUserResponse.getBody();
        Assert.assertNotNull(libraryUser.getUserId());
        Assert.assertTrue(libraryUser.getUsername().contains("test.user"));
        Assert.assertEquals("TestFn", libraryUser.getFirstName());
        Assert.assertEquals("TestLn", libraryUser.getLastName());
        Assert.assertNull(libraryUser.getRole());
    }

    @Test
    public void getUser_unauthorized_for_different_user() {

        String port = environment.getProperty("local.server.port");

        // First we register a user
        ResponseEntity<LibraryUser> response = registerNewUser();
        Assert.assertEquals(201, response.getStatusCodeValue());

        LibraryUser responseLibraryUser = response.getBody();
        Integer userId = responseLibraryUser.getUserId() + 1;

        // Login with the credentials
        ResponseEntity<String> loginResponse = loginUser(responseLibraryUser.getUsername(), responseLibraryUser.getPassword());
        Assert.assertEquals(200, loginResponse.getStatusCodeValue());

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
        Assert.assertEquals(401, libUserResponse.getStatusCodeValue());
    }

    @Test
    public void updateUser_success() {

        String port = environment.getProperty("local.server.port");

        // First we register a user
        ResponseEntity<LibraryUser> response = registerNewUser();

        Assert.assertEquals(201, response.getStatusCodeValue());

        LibraryUser responseLibraryUser = response.getBody();
        Assert.assertNotNull(responseLibraryUser);
        Integer userId = responseLibraryUser.getUserId();

        // Login with the credentials
        ResponseEntity<String> loginResponse = loginUser(responseLibraryUser.getUsername(), responseLibraryUser.getPassword());

        Assert.assertEquals(200, loginResponse.getStatusCodeValue());

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

        Assert.assertEquals(200, libUserResponse.getStatusCodeValue());

        // Now login with changed password
        loginResponse = loginUser(responseLibraryUser.getUsername(), responseLibraryUser.getPassword());
        Assert.assertEquals(200, loginResponse.getStatusCodeValue());

        // Now we get the user. Should have update email and phone number
        // Put the new Auth token
        headers.replace("Authorization", loginResponse.getHeaders().get("Authorization"));

       libUserResponse = testRestTemplate.exchange(
               userUri, HttpMethod.GET, new HttpEntity<Object>(headers),
                LibraryUser.class);

        Assert.assertEquals(200, libUserResponse.getStatusCodeValue());
        LibraryUser libraryUser = libUserResponse.getBody();
        Assert.assertEquals("newemailaddress@email.com", libraryUser.getEmailId());
        Assert.assertEquals("23423523", libraryUser.getPhoneNumber());
    }

    @Test
    public void updateUser_unauthorized_for_different_user() {

        String port = environment.getProperty("local.server.port");

        // First we register a user
        ResponseEntity<LibraryUser> response = registerNewUser();
        Assert.assertEquals(201, response.getStatusCodeValue());

        LibraryUser responseLibraryUser = response.getBody();
        Assert.assertNotNull(responseLibraryUser);
        // Update the details of another userId (userId + 1)
        Integer userId = responseLibraryUser.getUserId() + 1;

        // Login with the credentials to perform update
        ResponseEntity<String> loginResponse = loginUser(responseLibraryUser.getUsername(), responseLibraryUser.getPassword());
        Assert.assertEquals(200, loginResponse.getStatusCodeValue());

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
        Assert.assertEquals(401, libUserResponse.getStatusCodeValue());
    }

    @Test
    public void deleteUser_success() {

        String port = environment.getProperty("local.server.port");

        // First we register a user
        ResponseEntity<LibraryUser> response = registerNewUser();
        Assert.assertEquals(201, response.getStatusCodeValue());

        LibraryUser responseLibraryUser = response.getBody();
        Assert.assertNotNull(responseLibraryUser);
        Integer userId = responseLibraryUser.getUserId();

        // Login with the credentials
        ResponseEntity<String> loginResponse = loginUser(responseLibraryUser.getUsername(), responseLibraryUser.getPassword());
        Assert.assertEquals(200, loginResponse.getStatusCodeValue());

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

        Assert.assertEquals(202, libUserResponse.getStatusCodeValue());

        // Now login again. You should not be able to login
        loginResponse = loginUser(responseLibraryUser.getUsername(), responseLibraryUser.getPassword());
        Assert.assertEquals(403, loginResponse.getStatusCodeValue());

    }

    @Test
    public void deleteUser_unauthorized_for_different_user() {

        String port = environment.getProperty("local.server.port");

        // First we register a user
        ResponseEntity<LibraryUser> response = registerNewUser();
        Assert.assertEquals(201, response.getStatusCodeValue());

        LibraryUser responseLibraryUser = response.getBody();
        Assert.assertNotNull(responseLibraryUser);
        // Change the userId to something else
        Integer userId = responseLibraryUser.getUserId() + 1;

        // Login with the credentials
        ResponseEntity<String> loginResponse = loginUser(responseLibraryUser.getUsername(), responseLibraryUser.getPassword());
        Assert.assertEquals(200, loginResponse.getStatusCodeValue());

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

        Assert.assertEquals(401, libUserResponse.getStatusCodeValue());

    }

    @Test
    public void searchUsers_success() {

        // Register 10 users
        for(int i=0; i<10; i++) {
            registerNewUser();
        }

        URI searchUri = null;
        try {
            searchUri = new URI(TestConstants.USER_API_SEARCH_URL + "?firstName=" + TestConstants.TEST_USER_FIRST_NAME
            + "&lastName=" + TestConstants.TEST_USER_LAST_NAME);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ResponseEntity<LibraryUser[]> response = testRestTemplate.getForEntity(searchUri, LibraryUser[].class);

        Assert.assertEquals(200, response.getStatusCodeValue());
        Assert.assertEquals(10, response.getBody().length);
        for(LibraryUser libraryUser : response.getBody()) {
            Assert.assertEquals(TestConstants.TEST_USER_FIRST_NAME, libraryUser.getFirstName());
            Assert.assertEquals(TestConstants.TEST_USER_LAST_NAME, libraryUser.getLastName());
        }
    }

    @Test
    public void searchUsers_no_users() {

        // Register 10 users
        for(int i=0; i<10; i++) {
            registerNewUser();
        }

        URI searchUri = null;
        try {
            searchUri = new URI(TestConstants.USER_API_SEARCH_URL + "?firstName=BlahFn&lastName=BlahLn");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ResponseEntity<String> response = testRestTemplate.getForEntity(searchUri, String.class);
        Assert.assertEquals(404, response.getStatusCodeValue());
    }

    private ResponseEntity<LibraryUser> registerNewUser() {

        userCtr++;
        Gender gender = userCtr % 2 == 1 ? Gender.Male : Gender.Female;
        URI registerUri = null;
        try {
            registerUri = new URI(TestConstants.USER_API_REGISTER_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        LibraryUser newUser = new LibraryUser("test.user" + userCtr, TestConstants.TEST_USER_FIRST_NAME,
                TestConstants.TEST_USER_LAST_NAME, LocalDate.now().minusYears(30 + userCtr), gender, "123445556",
                "test.user" + userCtr + "@email.com");

        HttpEntity<LibraryUser> newUserRequest = new HttpEntity<>(newUser);

        return testRestTemplate.postForEntity(registerUri, newUserRequest, LibraryUser.class);
    }

    @Test
    public void test_login_successful() {

        // First we register a user
        ResponseEntity<LibraryUser> registerUserResponse = registerNewUser();

        Assert.assertEquals(201, registerUserResponse.getStatusCodeValue());

        LibraryUser responseLibraryUser = registerUserResponse.getBody();
        Assert.assertNotNull(responseLibraryUser);

        // Login with supplied username and default password
        ResponseEntity<String> loginResponse = loginUser(responseLibraryUser.getUsername(), responseLibraryUser.getPassword());

        Assert.assertEquals(200, loginResponse.getStatusCodeValue());
        String authToken = loginResponse.getHeaders().get("Authorization").get(0);
        Assert.assertNotNull(authToken);
        Assert.assertTrue(authToken.length() > 0);
    }

    @Test
    public void test_login_unsuccessful() {

        // Login with wrong credentials
        ResponseEntity<String> loginResponse = loginUser("blahblah", "blahblah");

        Assert.assertEquals(403, loginResponse.getStatusCodeValue());
        Assert.assertNull(loginResponse.getHeaders().get("Authorization"));
    }

    private ResponseEntity<String> loginUser(String username, String password) {

        String loginUrl = TestConstants.LOGIN_URL;
        URI loginUri = null;
        try {
            loginUri = new URI(loginUrl);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        HttpEntity<String> userLoginRequest = new HttpEntity<>(createLoginBody(username, password));
        return testRestTemplate.postForEntity(loginUri, userLoginRequest, String.class);

    }

    private String createLoginBody(String username, String password) {
        return "{ \"username\": \"" + username + "\", \"password\": \"" +  password + "\"}";
    }

}