package com.skb.course.apis.libraryapis.publisher;

import com.skb.course.apis.libraryapis.testutils.TestConstants;
import com.skb.course.apis.libraryapis.testutils.LibraryApiIntegrationTestUtil;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.net.URISyntaxException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PublisherControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private LibraryApiIntegrationTestUtil libraryApiIntegrationTestUtil;

    @Value("${library.api.user.admin.username}")
    private String adminUsername;

    @Value("${library.api.user.admin.password}")
    private String adminPassword;

    private static int publisherCtr;

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
    public void addPublisher_success() {

        // Login with the admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        ResponseEntity<Publisher> response = libraryApiIntegrationTestUtil.addNewPublisher(
                createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0)));
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Publisher responsePublisher = response.getBody();
        Assert.assertNotNull(responsePublisher);
        Assert.assertNotNull(responsePublisher.getPublisherId());
        Assert.assertTrue(responsePublisher.getName().contains(TestConstants.TEST_PUBLISHER_NAME));

    }

    @Test
    public void getPublisher_success_admin_user() {

        String port = environment.getProperty("local.server.port");

        // Login with the admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        MultiValueMap<String, String> header = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));
        // Add a Publisher
        ResponseEntity<Publisher> response = libraryApiIntegrationTestUtil.addNewPublisher(header);
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Now we get the Publisher
        URI getPublisherUri = null;
        try {
            getPublisherUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.PUBLISHER_API_BASE_URL + "/" + response.getBody().getPublisherId());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ResponseEntity<Publisher> libPublisherResponse = testRestTemplate.exchange(getPublisherUri, HttpMethod.GET,
                new HttpEntity<Object>(header), Publisher.class);

        Assert.assertEquals(HttpStatus.OK, libPublisherResponse.getStatusCode());
        Publisher responsePublisher = libPublisherResponse.getBody();
        Assert.assertNotNull(responsePublisher.getPublisherId());
        Assert.assertTrue(responsePublisher.getName().contains(TestConstants.TEST_PUBLISHER_NAME));
    }

    @Test
    public void getPublisher_success_normal_user() {

        String port = environment.getProperty("local.server.port");

        // Login with the admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        MultiValueMap<String, String> header = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));
        // Add a Publisher
        ResponseEntity<Publisher> response = libraryApiIntegrationTestUtil.addNewPublisher(header);
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Create a normal user
        ResponseEntity<LibraryUser> responseEntity = libraryApiIntegrationTestUtil.registerNewUser("get.publisher.success.normal.user");
        LibraryUser libraryUser = responseEntity.getBody();

        // Login with normal user credentials
        ResponseEntity<String> userLoginResponse = libraryApiIntegrationTestUtil.loginUser(libraryUser.getUsername(), libraryUser.getPassword());
        header = createAuthorizationHeader(userLoginResponse.getHeaders().get("Authorization").get(0));

        URI getPublisherUri = null;
        try {
            getPublisherUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.PUBLISHER_API_BASE_URL + "/" + response.getBody().getPublisherId());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ResponseEntity<Publisher> libPublisherResponse = testRestTemplate.exchange(getPublisherUri, HttpMethod.GET,
                new HttpEntity<Object>(header), Publisher.class);

        Assert.assertEquals(HttpStatus.OK, libPublisherResponse.getStatusCode());
        Publisher responsePublisher = libPublisherResponse.getBody();
        Assert.assertNotNull(responsePublisher.getPublisherId());
        Assert.assertTrue(responsePublisher.getName().contains(TestConstants.TEST_PUBLISHER_NAME));
    }

    @Test
    public void getPublisher_author_not_found() {

        String port = environment.getProperty("local.server.port");

        // Login with the admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        MultiValueMap<String, String> header = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));
        // Add a Publisher
        ResponseEntity<Publisher> response = libraryApiIntegrationTestUtil.addNewPublisher(header);
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Now we get the Publisher with and PublisherID that doesnot exist
        URI getPublisherUri = null;
        try {
            getPublisherUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.PUBLISHER_API_BASE_URL + "/12345");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ResponseEntity<Publisher> libPublisherResponse = testRestTemplate.exchange(getPublisherUri, HttpMethod.GET,
                new HttpEntity<Object>(header), Publisher.class);

        Assert.assertEquals(HttpStatus.NOT_FOUND, libPublisherResponse.getStatusCode());
    }


    @Test
    public void updatePublisher_success() {

        String port = environment.getProperty("local.server.port");

        // Login with the admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        MultiValueMap<String, String> header = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));
        // Add a Publisher
        ResponseEntity<Publisher> response = libraryApiIntegrationTestUtil.addNewPublisher(header);
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Publisher responsePublisher = response.getBody();
        Assert.assertNotNull(responsePublisher);

        // Set the values to be updated
        responsePublisher.setEmailId(TestConstants.TEST_PUBLISHER_EMAIL_UPDATED);
        responsePublisher.setPhoneNumber(TestConstants.TEST_PUBLISHER_PHONE_UPDATED);

        URI authorUri = null;
        try {
            authorUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.PUBLISHER_API_BASE_URL + "/" + response.getBody().getPublisherId());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        HttpEntity<Publisher> request = new HttpEntity<>(responsePublisher, header);
        ResponseEntity<Publisher> libPublisherResponse = testRestTemplate.exchange(
                authorUri, HttpMethod.PUT, request,
                Publisher.class);
        // Check if update was successful
        Assert.assertEquals(HttpStatus.OK, libPublisherResponse.getStatusCode());

        // Get the updated Publisher
       libPublisherResponse = testRestTemplate.exchange(
               authorUri, HttpMethod.GET, new HttpEntity<Object>(header),
                Publisher.class);

        Assert.assertEquals(HttpStatus.OK, libPublisherResponse.getStatusCode());
        Publisher publisher = libPublisherResponse.getBody();

        // Check if the response has updated details
        Assert.assertEquals(TestConstants.TEST_PUBLISHER_EMAIL_UPDATED, publisher.getEmailId());
        Assert.assertEquals(TestConstants.TEST_PUBLISHER_PHONE_UPDATED, publisher.getPhoneNumber());
    }

    @Test
    public void updatePublisher_failure_update_wrong_publisher() {

        String port = environment.getProperty("local.server.port");

        // Login with the admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        MultiValueMap<String, String> header = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));
        // Add a Publisher
        ResponseEntity<Publisher> response = libraryApiIntegrationTestUtil.addNewPublisher(header);
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Publisher responsePublisher = response.getBody();
        Assert.assertNotNull(responsePublisher);

        // Set the values to be updated
        responsePublisher.setEmailId("publishernewemail@email.com");
        responsePublisher.setPhoneNumber("99887766");

        URI authorUri = null;
        try {
            authorUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.PUBLISHER_API_BASE_URL + "/1234");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        HttpEntity<Publisher> request = new HttpEntity<>(responsePublisher, header);
        ResponseEntity<Publisher> libPublisherResponse = testRestTemplate.exchange(
                authorUri, HttpMethod.PUT, request,
                Publisher.class);
        // Check if update was successful
        Assert.assertEquals(HttpStatus.BAD_REQUEST, libPublisherResponse.getStatusCode());

    }

    @Test
    public void updatePublisher_normal_user_unauthorized() {

        String port = environment.getProperty("local.server.port");

        // Login with the admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        MultiValueMap<String, String> header = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));
        // Add a Publisher
        ResponseEntity<Publisher> response = libraryApiIntegrationTestUtil.addNewPublisher(header);
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Update the Publisher. Set the values to be updated
        Publisher responsePublisher = response.getBody();
        responsePublisher.setEmailId("publishernewemail@email.com");
        responsePublisher.setPhoneNumber("99887766");

        // Create a normal user
        ResponseEntity<LibraryUser> responseEntity = libraryApiIntegrationTestUtil.registerNewUser("update.publisher.success.normal.user");
        LibraryUser libraryUser = responseEntity.getBody();

        // Login with normal user credentials
        ResponseEntity<String> userLoginResponse = libraryApiIntegrationTestUtil.loginUser(libraryUser.getUsername(), libraryUser.getPassword());
        header = createAuthorizationHeader(userLoginResponse.getHeaders().get("Authorization").get(0));

        URI authorUri = null;
        try {
            authorUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.PUBLISHER_API_BASE_URL + "/" + responsePublisher.getPublisherId());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // Send update request with normal User credential
        HttpEntity<Publisher> request = new HttpEntity<>(responsePublisher, header);
        ResponseEntity<Publisher> libPublisherResponse = testRestTemplate.exchange(
                authorUri, HttpMethod.PUT, request,
                Publisher.class);
        // Check if update was successful
        Assert.assertEquals(HttpStatus.FORBIDDEN, libPublisherResponse.getStatusCode());
    }

    @Test
    public void deletePublisher_success() {

        String port = environment.getProperty("local.server.port");

        // Login with the admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        MultiValueMap<String, String> header = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));
        // Add a Publisher
        ResponseEntity<Publisher> response = libraryApiIntegrationTestUtil.addNewPublisher(header);
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Publisher responsePublisher = response.getBody();
        Assert.assertNotNull(responsePublisher);

        URI authorUri = null;
        try {
            authorUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.PUBLISHER_API_BASE_URL + "/" + response.getBody().getPublisherId());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        HttpEntity<Publisher> request = new HttpEntity<>(responsePublisher, header);
        ResponseEntity<String> deleteResponse = testRestTemplate.exchange(
                authorUri, HttpMethod.DELETE, request,
                String.class);
        // Check if update was successful
        Assert.assertEquals(HttpStatus.ACCEPTED, deleteResponse.getStatusCode());

        // Get the updated Publisher
        ResponseEntity<Publisher> getResponse = testRestTemplate.exchange(
                authorUri, HttpMethod.GET, new HttpEntity<Object>(header),
                Publisher.class);

        // Publisher should not exist
        Assert.assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());

    }

    @Test
    public void deletePublisher_failure_author_not_found() {

        String port = environment.getProperty("local.server.port");

        // Login with the admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        MultiValueMap<String, String> header = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));
        // Add a Publisher
        ResponseEntity<Publisher> response = libraryApiIntegrationTestUtil.addNewPublisher(header);
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Publisher responsePublisher = response.getBody();
        Assert.assertNotNull(responsePublisher);

        URI authorUri = null;
        try {
            authorUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.PUBLISHER_API_BASE_URL + "/1234");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        HttpEntity<Publisher> request = new HttpEntity<>(responsePublisher, header);
        ResponseEntity<String> deleteResponse = testRestTemplate.exchange(
                authorUri, HttpMethod.DELETE, request,
                String.class);
        // Check if update was successful
        Assert.assertEquals(HttpStatus.NOT_FOUND, deleteResponse.getStatusCode());

    }

    @Test
    public void deletePublisher_normal_user_unauthorized() {

        String port = environment.getProperty("local.server.port");

        // Login with the admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        MultiValueMap<String, String> header = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));
        // Add a Publisher
        ResponseEntity<Publisher> response = libraryApiIntegrationTestUtil.addNewPublisher(header);
        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Update the Publisher. Set the values to be updated
        Publisher responsePublisher = response.getBody();

        // Create a normal user
        ResponseEntity<LibraryUser> responseEntity = libraryApiIntegrationTestUtil.registerNewUser("delete.publisher.success.normal.user");
        LibraryUser libraryUser = responseEntity.getBody();

        // Login with normal user credentials
        ResponseEntity<String> userLoginResponse = libraryApiIntegrationTestUtil.loginUser(libraryUser.getUsername(), libraryUser.getPassword());
        header = createAuthorizationHeader(userLoginResponse.getHeaders().get("Authorization").get(0));

        URI authorUri = null;
        try {
            authorUri = new URI(TestConstants.API_BASE_URL + port + TestConstants.PUBLISHER_API_BASE_URL + "/" + responsePublisher.getPublisherId());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // Send update request with normal User credential
        HttpEntity<Publisher> request = new HttpEntity<>(responsePublisher, header);
        ResponseEntity<Publisher> libPublisherResponse = testRestTemplate.exchange(
                authorUri, HttpMethod.DELETE, request,
                Publisher.class);
        // Check if update was successful
        Assert.assertEquals(HttpStatus.FORBIDDEN, libPublisherResponse.getStatusCode());
    }

    @Test
    public void searchPublisher_success() {

        String port = environment.getProperty("local.server.port");

        // Login with the admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        MultiValueMap<String, String> header = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));
        // Add a Publishers
        for(int i=0; i<10; i++) {
            libraryApiIntegrationTestUtil.addNewPublisher(header);
        }

        URI publisherSearchUri = null;
        try {
            publisherSearchUri = new URI(TestConstants.PUBLISHER_API_SEARCH_URL +
                    "?name=" + TestConstants.TEST_PUBLISHER_NAME);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ResponseEntity<Publisher[]> response = testRestTemplate.exchange(publisherSearchUri, HttpMethod.GET,
                new HttpEntity<Object>(header), Publisher[].class);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        // Comparing with >= 10 because depending upon which other test methods run the number of Publishers may vary
        Assert.assertTrue(response.getBody().length >= 10);
        for(Publisher Publisher : response.getBody()) {
            Assert.assertTrue(Publisher.getName().contains(TestConstants.TEST_PUBLISHER_NAME));
        }

    }

    @Test
    public void searchPublishers_no_Publishers() {

        // Login with the admin credentials
        ResponseEntity<String> loginResponse = libraryApiIntegrationTestUtil.loginUser(adminUsername, adminPassword);
        Assert.assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        MultiValueMap<String, String> header = createAuthorizationHeader(loginResponse.getHeaders().get("Authorization").get(0));
        // Add a Publishers
        for(int i=0; i<10; i++) {
            libraryApiIntegrationTestUtil.addNewPublisher(header);
        }

        URI publisherSearchUri = null;
        try {
            publisherSearchUri = new URI(TestConstants.PUBLISHER_API_SEARCH_URL +
                    "?name=BlahBlah");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ResponseEntity<String> response = testRestTemplate.exchange(publisherSearchUri, HttpMethod.GET,
                new HttpEntity<Object>(header), String.class);

        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    private MultiValueMap<String, String> createAuthorizationHeader(String bearerToken) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", bearerToken);

        return headers;
    }

}