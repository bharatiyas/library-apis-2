package com.skb.course.apis.libraryapis.model;

import java.io.Serializable;

public class Book implements Serializable {

    private long bookId;
    private String isbn;
    private String title;
    private String numberOfCopiesAvailable;
    private String numberOfCopiesIssued;
    private int yearPublished;
    private String edition;

    public Book() {
    }

    public Book(String isbn, String title, String numberOfCopiesAvailable, String numberOfCopiesIssued, int yearPublished, String edition) {
        this.isbn = isbn;
        this.title = title;
        this.numberOfCopiesAvailable = numberOfCopiesAvailable;
        this.numberOfCopiesIssued = numberOfCopiesIssued;
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

    public String getNumberOfCopiesAvailable() {
        return numberOfCopiesAvailable;
    }

    public String getNumberOfCopiesIssued() {
        return numberOfCopiesIssued;
    }

    public int getYearPublished() {
        return yearPublished;
    }

    public String getEdition() {
        return edition;
    }

    public void setNumberOfCopiesAvailable(String numberOfCopiesAvailable) {
        this.numberOfCopiesAvailable = numberOfCopiesAvailable;
    }

    public void setNumberOfCopiesIssued(String numberOfCopiesIssued) {
        this.numberOfCopiesIssued = numberOfCopiesIssued;
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookId=" + bookId +
                ", isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", numberOfCopiesAvailable='" + numberOfCopiesAvailable + '\'' +
                ", numberOfCopiesIssued='" + numberOfCopiesIssued + '\'' +
                ", yearPublished=" + yearPublished +
                ", edition='" + edition + '\'' +
                '}';
    }
}
