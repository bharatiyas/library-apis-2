package com.skb.course.apis.libraryapis;

import com.skb.course.apis.libraryapis.entity.AuthorEntity;
import com.skb.course.apis.libraryapis.entity.BookEntity;
import com.skb.course.apis.libraryapis.entity.BookStatusEntity;
import com.skb.course.apis.libraryapis.entity.PublisherEntity;
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
import java.util.Optional;

@Component
public class LibraryApiTestUtil {

    @Autowired
    public TestRestTemplate testRestTemplate;

    public static int userCtr;
    public static int authorCtr;
    public static int publisherCtr;
    public static int bookCtr;

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

    public static PublisherEntity createPublisherEntity() {
        return new PublisherEntity(TestConstants.TEST_PUBLISHER_NAME, TestConstants.TEST_PUBLISHER_EMAIL, TestConstants.TEST_PUBLISHER_PHONE);
    }

    public static Optional<PublisherEntity> createPublisherEntityOptional() {
        return Optional.of(createPublisherEntity());
    }

    public static BookEntity createBookEntity() {
        BookEntity be = new BookEntity(TestConstants.TEST_BOOK_ISBN, TestConstants.TEST_BOOK_TITLE,
                TestConstants.TEST_BOOK_YEAR_PUBLISHED, TestConstants.TEST_BOOK_EDITION);
        be.setPublisher(createPublisherEntity());
        return be;
    }

    public static Optional<BookEntity> createBookEntityOptional() {
        return Optional.of(createBookEntity());
    }

    public static BookStatusEntity createBookStatusEntity(int bookId) {
        return new BookStatusEntity(bookId, BookStatusState.Active, 3, 0);
    }

    public static Optional<BookStatusEntity> createBookStatusEntityOptional(int bookId) {
        return Optional.of(createBookStatusEntity(bookId));
    }

    public static AuthorEntity createAuthorEntity() {
        return new AuthorEntity(TestConstants.TEST_AUTHOR_FIRST_NAME + authorCtr,
                TestConstants.TEST_AUTHOR_LAST_NAME, LocalDate.now().minusYears(30 + authorCtr), Gender.Female);
    }

    public static Optional<AuthorEntity> createAuthorEntityOptional() {
        return Optional.of(createAuthorEntity());
    }
}
