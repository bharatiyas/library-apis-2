package com.skb.course.apis.libraryapis.model;

import com.skb.course.apis.libraryapis.entity.AuthorEntity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Book implements Serializable {

    private int bookId;
    private String isbn;
    private String title;
    private int publisherId;
    private Integer yearPublished;
    private String edition;
    private BookStatus bookStatus;
    private Set<AuthorEntity> authors = new HashSet<>();

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

    public BookStatus getBookStatus() {
        return bookStatus;
    }

    public void setBookStatus(BookStatus bookStatus) {
        this.bookStatus = bookStatus;
    }

    public Set<AuthorEntity> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<AuthorEntity> authors) {
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
