package com.skb.course.apis.libraryapis.model;

public class BookStatus {

    private int bookId;
    private BookStatusState state;
    private int numberOfCopiesAvailable;
    private int numberOfCopiesIssued;

    public BookStatus() {
    }

    public BookStatus(int bookId, BookStatusState state, int numberOfCopiesAvailable, int numberOfCopiesIssued) {
        this.bookId = bookId;
        this.state = state;
        this.numberOfCopiesAvailable = numberOfCopiesAvailable;
        this.numberOfCopiesIssued = numberOfCopiesIssued;
    }

    public BookStatus(BookStatusState state, int numberOfCopiesAvailable, int numberOfCopiesIssued) {
        this.state = state;
        this.numberOfCopiesAvailable = numberOfCopiesAvailable;
        this.numberOfCopiesIssued = numberOfCopiesIssued;
    }

    public BookStatusState getState() {
        return state;
    }

    public void setState(BookStatusState state) {
        this.state = state;
    }

    public int getNumberOfCopiesAvailable() {
        return numberOfCopiesAvailable;
    }

    public void setNumberOfCopiesAvailable(int numberOfCopiesAvailable) {
        this.numberOfCopiesAvailable = numberOfCopiesAvailable;
    }

    public int getNumberOfCopiesIssued() {
        return numberOfCopiesIssued;
    }

    public void setNumberOfCopiesIssued(int numberOfCopiesIssued) {
        this.numberOfCopiesIssued = numberOfCopiesIssued;
    }
}

