package com.skb.course.apis.libraryapis.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.skb.course.apis.libraryapis.entity.AuthorEntity;

import javax.validation.Valid;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Book implements Serializable {

    private Integer bookId;

    @Size(min = 1, max = 50, message
            = "ISBN must be between 1 and 50 characters")
    private String isbn;

    @Size(min = 1, max = 50, message
            = "Title must be between 1 and 50 characters")
    private String title;
    private Integer publisherId;

    // Assuming that the year the book was published would be after 1990. Not too old books
    @Pattern(regexp = "^(199[0-9]|200[0-9]|201[0-9])$")
    private Integer yearPublished;

    @Size(min = 1, max = 50, message
            = "Edition must be between 1 and 20 characters")
    private String edition;

    @Valid
    private BookStatus bookStatus;
    private Set<Author> authors = new HashSet<>();

    public Book() {
    }

    public Book(String isbn, String title, int publisherId, int yearPublished, String edition, BookStatus bookStatus) {
        this.isbn = isbn;
        this.title = title;
        this.publisherId = publisherId;
        this.yearPublished = yearPublished;
        this.edition = edition;
        this.bookStatus = bookStatus;
    }

    public Book(int bookId, String isbn, String title, int publisherId, int yearPublished, String edition, BookStatus bookStatus) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
        this.publisherId = publisherId;
        this.yearPublished = yearPublished;
        this.edition = edition;
        this.bookStatus = bookStatus;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public Integer getYearPublished() {
        return yearPublished;
    }

    public void setYearPublished(int yearPublished) {
        this.yearPublished = yearPublished;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public Integer getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(int publisherId) {
        this.publisherId = publisherId;
    }

    public BookStatus getBookStatus() {
        return bookStatus;
    }

    public void setBookStatus(BookStatus bookStatus) {
        this.bookStatus = bookStatus;
    }

    public Set<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<Author> authors) {
        this.authors = authors;
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookId=" + bookId +
                ", isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", publisherId=" + publisherId +
                ", yearPublished=" + yearPublished +
                ", edition='" + edition + '\'' +
                '}';
    }
}
