package com.skb.course.apis.libraryapis.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.GeneratorType;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "BOOK")
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"})
public class BookEntity {

    @Column(name = "Book_Id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bookId_generator")
    @SequenceGenerator(name="bookId_generator", sequenceName = "book_sequence", allocationSize = 50)
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

    @OneToOne
    private PublisherEntity publisher = new PublisherEntity();

    @ManyToMany
    // This will create only 1 mapping table named: AUTHOR_BOOK having 2 columns = BOOK_ID, AUTHOR_ID
    @JoinTable(name = "author_book", joinColumns = @JoinColumn(name = "book_id"), inverseJoinColumns = @JoinColumn(name = "author_id"))
    private Set<AuthorEntity> authors = new HashSet<>();

    public BookEntity(String isbn, String title, String numberOfCopiesAvailable, String numberOfCopiesIssued,
                      long publisherId, int yearPublished, String edition) {
        this.isbn = isbn;
        this.title = title;
        this.numberOfCopiesAvailable = numberOfCopiesAvailable;
        this.numberOfCopiesIssued = numberOfCopiesIssued;
        this.publisherId = publisherId;
        this.yearPublished = yearPublished;
        this.edition = edition;
    }

    public BookEntity(String isbn, String title, String numberOfCopiesAvailable, String numberOfCopiesIssued,
                      long publisherId, int yearPublished, String edition, PublisherEntity publisher,
                      Set<AuthorEntity> authors) {
        this.isbn = isbn;
        this.title = title;
        this.numberOfCopiesAvailable = numberOfCopiesAvailable;
        this.numberOfCopiesIssued = numberOfCopiesIssued;
        this.publisherId = publisherId;
        this.yearPublished = yearPublished;
        this.edition = edition;
        this.publisher = publisher;
        this.authors = authors;
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

    public void setPublisherId(long publisherId) {
        this.publisherId = publisherId;
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
}

