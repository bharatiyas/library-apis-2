package com.skb.course.apis.libraryapis.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
@Table(name = "AUTHOR")
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"})
public class AuthorEntity {

    @Column(name = "Book_Id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bookId_generator")
    @SequenceGenerator(name="bookId_generator", sequenceName = "books_sequence", allocationSize = 50)
    private long bookId;

    @Column(name = "ISBN")
    private String isbn;

    @Column(name = "Title")
    private String title;

    @Column(name = "Number_Of_Copies_Available")
    private String numberOfCopiesAvailable;

    @Column(name = "Number_Of_Copies_Issued")
    private String numberOfCopiesIssued;

    private long publisherId;

    @Column(name = "Year_Published")
    private int yearPublished;

    @Column(name = "Edition")
    private String edition;

    public AuthorEntity(String isbn, String title, String numberOfCopiesAvailable, String numberOfCopiesIssued,
                        long publisherId, int yearPublished, String edition) {
        this.isbn = isbn;
        this.title = title;
        this.numberOfCopiesAvailable = numberOfCopiesAvailable;
        this.numberOfCopiesIssued = numberOfCopiesIssued;
        this.publisherId = publisherId;
        this.yearPublished = yearPublished;
        this.edition = edition;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNumberOfCopiesAvailable() {
        return numberOfCopiesAvailable;
    }

    public void setNumberOfCopiesAvailable(String numberOfCopiesAvailable) {
        this.numberOfCopiesAvailable = numberOfCopiesAvailable;
    }

    public String getNumberOfCopiesIssued() {
        return numberOfCopiesIssued;
    }

    public void setNumberOfCopiesIssued(String numberOfCopiesIssued) {
        this.numberOfCopiesIssued = numberOfCopiesIssued;
    }

    public long getPublisherId() {
        return publisherId;
    }

    public int getYearPublished() {
        return yearPublished;
    }

    public String getEdition() {
        return edition;
    }
}

