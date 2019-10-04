package com.skb.course.apis.libraryapis.testutils;

import com.skb.course.apis.libraryapis.model.common.Gender;

public class TestConstants {

    // Base URL
    public static final String API_BASE_URL = "http://localhost:";

    // User API URLs
    public static final String USER_API_BASE_URL = "/users";
    public static final String USER_API_REGISTER_URL = USER_API_BASE_URL + "/register";
    public static final String USER_API_SEARCH_URL = USER_API_BASE_URL + "/search";
    public static final String USER_API_ISSUE_BOOK_URL = "/books";

    // Test User Details
    public static final String TEST_USER_FIRST_NAME = "TestUserFn";
    public static final String TEST_USER_LAST_NAME = "TestUserLn";
    public static final String TEST_USER_USERNAME = "test.username";
    public static final String TEST_USER_PASSWORD = "test.password";
    public static final String TEST_USER_EMAIL = TEST_USER_USERNAME + "@email.con";
    public static final String TEST_USER_PHONE = "223-344-566";
    public static final String TEST_USER_PHONE_UPDATED = "111-344-908";
    public static final Gender TEST_USER_GENDER = Gender.Female;

    // Author API URLs
    public static final String AUTHOR_API_BASE_URL = "/authors";
    public static final String AUTHOR_API_SEARCH_URL = AUTHOR_API_BASE_URL + "/search";

    // Test Author Details
    public static final String TEST_AUTHOR_FIRST_NAME = "TestAuthorFn";
    public static final String TEST_AUTHOR_LAST_NAME = "TestAuthorLn";

    // Publisher API URLs
    public static final String PUBLISHER_API_BASE_URL = "/publishers";
    public static final String PUBLISHER_API_SEARCH_URL = PUBLISHER_API_BASE_URL + "/search";

    // Test Publisher Details
    public static final String TEST_PUBLISHER_NAME = "TestPublisherName";
    public static final String TEST_PUBLISHER_EMAIL = "TestPublisher@email.com";
    public static final String TEST_PUBLISHER_PHONE = "112-233-455";
    public static final String TEST_PUBLISHER_EMAIL_UPDATED = "TestPublisherNew@email.com";
    public static final String TEST_PUBLISHER_PHONE_UPDATED = "554-433-211";

    // Book API URLs
    public static final String BOOK_API_BASE_URL = "/books";
    public static final String BOOK_ADD_AUTHOR_API_URL = "/authors";
    public static final String BOOK_API_GET_BY_ISBN_URL = BOOK_API_BASE_URL + "/isbn/";
    public static final String BOOK_API_SEARCH_URL = BOOK_API_BASE_URL + "/search";

    // Test Book Details
    public static final String TEST_BOOK_ISBN = "978-3-16-148410-";
    public static final String TEST_BOOK_TITLE = "SpringBoot Is Fun";
    public static final int TEST_BOOK_YEAR_PUBLISHED = 2010;
    public static final String TEST_BOOK_EDITION = "First Edition";


    // Login URL
    public static final String LOGIN_URL = "/login";

    // Common
    public static final String API_TRACE_ID = "library-api-unique-trace-id";
}
