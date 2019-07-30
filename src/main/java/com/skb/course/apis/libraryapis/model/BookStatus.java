package com.skb.course.apis.libraryapis.model;

public class BookStatus {

    private Integer bookId;
    private BookStatusState state;
    private Integer totalNumberOfCopies;
    private Integer numberOfCopiesIssued;

    public BookStatus() {
    }

    public BookStatus(int bookId, BookStatusState state, int totalNumberOfCopies, int numberOfCopiesIssued) {
        this.bookId = bookId;
        this.state = state;
        this.totalNumberOfCopies = totalNumberOfCopies;
        this.numberOfCopiesIssued = numberOfCopiesIssued;
    }

    public BookStatus(BookStatusState state, int totalNumberOfCopies, int numberOfCopiesIssued) {
        this.state = state;
        this.totalNumberOfCopies = totalNumberOfCopies;
        this.numberOfCopiesIssued = numberOfCopiesIssued;
    }

    public BookStatusState getState() {
        return state;
    }

    public void setState(BookStatusState state) {
        this.state = state;
    }

    public int getTotalNumberOfCopies() {
        return totalNumberOfCopies;
    }

    public void setTotalNumberOfCopies(int totalNumberOfCopies) {
        this.totalNumberOfCopies = totalNumberOfCopies;
    }

    public int getNumberOfCopiesIssued() {
        return numberOfCopiesIssued;
    }

    public void setNumberOfCopiesIssued(int numberOfCopiesIssued) {
        this.numberOfCopiesIssued = numberOfCopiesIssued;
    }
}

