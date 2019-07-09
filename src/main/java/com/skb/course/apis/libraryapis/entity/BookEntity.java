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
    private int bookId;

    @Column(name = "ISBN")
    private String isbn;

    @Column(name = "Title")
    private String title;

    private int publisherId;

    @Column(name = "Year_Published")
    private int yearPublished;

    @Column(name = "Edition")
    private String edition;

    @OneToOne
    private PublisherEntity publisher = new PublisherEntity();

    @OneToOne
    private BookStatusEntity bookStatus = new BookStatusEntity();

    public BookEntity() {
    }

    @ManyToMany
    // This will create only 1 mapping table named: AUTHOR_BOOK having 2 columns = BOOK_ID, AUTHOR_ID
    @JoinTable(name = "book_author", joinColumns = @JoinColumn(name = "book_id"), inverseJoinColumns = @JoinColumn(name = "author_id"))
    private Set<AuthorEntity> authors = new HashSet<>();

    public BookEntity(String isbn, String title, int publisherId, int yearPublished, String edition) {
        this.isbn = isbn;
        this.title = title;
        this.publisherId = publisherId;
        this.yearPublished = yearPublished;
        this.edition = edition;
    }

    public BookEntity(String isbn, String title, int publisherId, int yearPublished, String edition,
                      PublisherEntity publisher, Set<AuthorEntity> authors, BookStatusEntity bookStatus) {
        this.isbn = isbn;
        this.title = title;
        this.publisherId = publisherId;
        this.yearPublished = yearPublished;
        this.edition = edition;
        this.publisher = publisher;
        this.authors = authors;
        this.bookStatus = bookStatus;
    }

    public int getBookId() {
        return bookId;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public int getPublisherId() {
        return publisherId;
    }

    public int getYearPublished() {
        return yearPublished;
    }

    public String getEdition() {
        return edition;
    }

    public void setPublisherId(int publisherId) {
        this.publisherId = publisherId;
    }

    public void setYearPublished(int yearPublished) {
        this.yearPublished = yearPublished;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public BookStatusEntity getBookStatus() {
        return bookStatus;
    }

    public void setBookStatus(BookStatusEntity bookStatus) {
        this.bookStatus = bookStatus;
    }

    public Set<AuthorEntity> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<AuthorEntity> authors) {
        this.authors = authors;
    }
}

