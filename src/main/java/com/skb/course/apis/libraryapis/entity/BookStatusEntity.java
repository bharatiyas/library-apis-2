package com.skb.course.apis.libraryapis.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.skb.course.apis.libraryapis.model.BookStatusState;

import javax.persistence.*;

@Entity
@Table(name = "BOOK_STATUS")
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"})
public class BookStatusEntity {

    @Column(name = "Book_Id")
    @Id
    private int bookId;

    @Column(name = "State")
    @Enumerated(EnumType.STRING)
    private BookStatusState state;

    @Column(name = "Number_Of_Copies_Available")
    private int numberOfCopiesAvailable;

    @Column(name = "Number_Of_Copies_Issued")
    private int numberOfCopiesIssued;

    public BookStatusEntity() {
    }

    public BookStatusEntity(int bookId, BookStatusState state, int numberOfCopiesAvailable, int numberOfCopiesIssued) {
        this.bookId = bookId;
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

