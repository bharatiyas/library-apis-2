package com.skb.course.apis.libraryapis.controller;

import com.skb.course.apis.libraryapis.TestConstants;
import com.skb.course.apis.libraryapis.model.Gender;
import com.skb.course.apis.libraryapis.model.LibraryUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;

@Component
public class LibraryApiTestUtil {

    @Autowired
    private TestRestTemplate testRestTemplate;

    private static int userCtr;

    public ResponseEntity<String> loginUser(String username, String password) {

        String loginUrl = TestConstants.LOGIN_URL;
        URI loginUri = null;
        try {
            loginUri = new URI(loginUrl);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        HttpEntity<String> loginRequest = new HttpEntity<>(createLoginBody(username, password));
        return testRestTemplate.postForEntity(loginUri, loginRequest, String.class);

    }

    public ResponseEntity<LibraryUser> registerNewUser() {

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

    private String createLoginBody(String username, String password) {
        return "{ \"username\": \"" + username + "\", \"password\": \"" +  password + "\"}";
    }
}
