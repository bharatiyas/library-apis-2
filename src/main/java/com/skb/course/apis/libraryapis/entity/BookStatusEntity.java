package com.skb.course.apis.libraryapis.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
@Table(name = "BOOK_STATUS")
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"})
public class BookStatusEntity {

    @Column(name = "Id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "book_status_generator")
    @SequenceGenerator(name="book_status_generator", sequenceName = "book_status_sequence", allocationSize = 50)
    private long id;

    @Column(name = "Status")
    private String status;

    @Column(name = "Number_Of_Copies_Available")
    private String numberOfCopiesAvailable;

    @Column(name = "Number_Of_Copies_Issued")
    private String numberOfCopiesIssued;

    public BookStatusEntity() {
    }

    public BookStatusEntity(String status, String numberOfCopiesAvailable, String numberOfCopiesIssued) {
        this.status = status;
        this.numberOfCopiesAvailable = numberOfCopiesAvailable;
        this.numberOfCopiesIssued = numberOfCopiesIssued;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
}

