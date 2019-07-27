package com.skb.course.apis.libraryapis.entity;

import com.skb.course.apis.libraryapis.model.BookStatusState;

import javax.persistence.*;

@Entity
@Table(name = "BOOK_STATUS")
public class BookStatusEntity {

    @Column(name = "Book_Id")
    @Id
    private int bookId;

    // BookStatusEntity has a 1-1 relationship with BookEntity, where BookEntity is the parent and Book_Id column
    // is the FK on the BookStatus table
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Book_Id", nullable = false)
    private BookEntity bookEntity;

    @Column(name = "State")
    @Enumerated(EnumType.STRING)
    private BookStatusState state;

    @Column(name = "Total_Number_Of_Copies")
    private int totalNumberOfCopies;

    @Column(name = "Number_Of_Copies_Issued")
    private int numberOfCopiesIssued;

    public BookStatusEntity() {
    }

    public BookStatusEntity(int bookId, BookStatusState state, int totalNumberOfCopies, int numberOfCopiesIssued) {
        this.bookId = bookId;
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

    public BookEntity getBookEntity() {
        return bookEntity;
    }

    public void setBookEntity(BookEntity bookEntity) {
        this.bookEntity = bookEntity;
    }
}

