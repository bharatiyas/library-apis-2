package com.skb.course.apis.libraryapis;

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
public class LibraryApiTestUtil {

    @Autowired
    private TestRestTemplate testRestTemplate;

    private static int userCtr;
    private static int authorCtr;
    private static int publisherCtr;
    private static int bookCtr;

    public static LibraryUser createUser(String username) {

        userCtr++;
        Gender gender = userCtr % 2 == 1 ? Gender.Male : Gender.Female;
        return new LibraryUser(username, TestConstants.TEST_USER_FIRST_NAME,
                TestConstants.TEST_USER_LAST_NAME, LocalDate.now().minusYears(30 + userCtr), gender, "123445556",
                username + "@email.com");

    }

    public static Author createAuthor() {

        authorCtr++;
        Gender gender = authorCtr % 2 == 1 ? Gender.Male : Gender.Female;

        // Create a new Author object
        return new Author(TestConstants.TEST_AUTHOR_FIRST_NAME + authorCtr,
                TestConstants.TEST_AUTHOR_LAST_NAME, LocalDate.now().minusYears(30 + authorCtr), gender);

    }

    public static Publisher createPublisher() {

        publisherCtr++;

        // Create a new Publisher object
        return new Publisher(TestConstants.TEST_PUBLISHER_NAME + publisherCtr,
                TestConstants.TEST_PUBLISHER_EMAIL, TestConstants.TEST_PUBLISHER_PHONE);

    }

    public static Book createBook(int publisherId) {

        bookCtr++;

        // Create a new Publisher object
        return new Book(TestConstants.TEST_BOOK_ISBN + bookCtr,
                TestConstants.TEST_BOOK_TITLE + "-" + bookCtr, publisherId,
                TestConstants.TEST_BOOK_YEAR_PUBLISHED, TestConstants.TEST_BOOK_EDITION, createBookStatus());

    }

    public static BookStatus createBookStatus() {
        return new BookStatus(BookStatusState.Active, 3, 0);
    }
}
