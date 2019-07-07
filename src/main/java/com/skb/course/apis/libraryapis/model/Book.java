package com.skb.course.apis.libraryapis.model;

import java.io.Serializable;

public class Book implements Serializable {

    private int bookId;
    private String isbn;
    private String title;
    private long publisherId;
    private int yearPublished;
    private String edition;
    private BookStatus bookStatus;

    public Book() {
    }

    public Book(String isbn, String title, long publisherId, int yearPublished, String edition, BookStatus bookStatus) {
        this.isbn = isbn;
        this.title = title;
        this.publisherId = publisherId;
        this.yearPublished = yearPublished;
        this.edition = edition;
        this.bookStatus = bookStatus;
    }

    public Book(int bookId, String isbn, String title, long publisherId, int yearPublished, String edition, BookStatus bookStatus) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
        this.publisherId = publisherId;
        this.yearPublished = yearPublished;
        this.edition = edition;
        this.bookStatus = bookStatus;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getYearPublished() {
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

    public long getPublisherId() {
        return publisherId;
    }

    public BookStatus getBookStatus() {
        return bookStatus;
    }

    public void setBookStatus(BookStatus bookStatus) {
        this.bookStatus = bookStatus;
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
