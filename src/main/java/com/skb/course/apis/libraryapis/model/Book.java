package com.skb.course.apis.libraryapis.model;

import java.io.Serializable;

public class Book implements Serializable {

    private long bookId;
    private String isbn;
    private String title;
    private int yearPublished;
    private String edition;

    public Book() {
    }

    public Book(String isbn, String title, int yearPublished, String edition) {
        this.isbn = isbn;
        this.title = title;
        this.yearPublished = yearPublished;
        this.edition = edition;
    }

    public long getBookId() {
        return bookId;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public int getYearPublished() {
        return yearPublished;
    }

    public String getEdition() {
        return edition;
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookId=" + bookId +
                ", isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", yearPublished=" + yearPublished +
                ", edition='" + edition + '\'' +
                '}';
    }
}
