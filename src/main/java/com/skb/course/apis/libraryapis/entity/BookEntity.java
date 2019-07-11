package com.skb.course.apis.libraryapis.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "BOOK")
public class BookEntity {

    @Column(name = "Book_Id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bookId_generator")
    @SequenceGenerator(name="bookId_generator", sequenceName = "book_sequence", allocationSize = 1)
    private int bookId;

    @Column(name = "ISBN")
    private String isbn;

    @Column(name = "Title")
    private String title;

    @Column(name = "Year_Published")
    private int yearPublished;

    @Column(name = "Edition")
    private String edition;

    // BookEntity has a M-1 relationship with PublisherEntity, where PublisherEntity is the parent and Publisher_Id column
    // is the FK referring to Publisher table. Notice that we do not have any publisherId field.
    // FetchType.LAZY - Fetch the related entity lazily from the database.
    @ManyToOne(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinColumn(name = "Publisher_Id",
            nullable = false)
    private PublisherEntity publisher;

    // BookStatusEntity has a 1-1 relationship with BookEntity, where BookEntity is the parent
    // bookEntity property in BookStatusEntity class maps to BookEntity
    // We use mappedBy attribute in the BookEntity to tell hibernate that the BookEntity is not responsible for this
    // relationship. Therefore, Hibernate should look for a field named "bookEntity" in the BookStatusEntity to find the
    // configuration for the JoinColumn/ForeignKey column.
    @OneToOne(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            mappedBy = "bookEntity")
    private BookStatusEntity bookStatus;

    // BookEntity has M-M relationship with AuthorEntity
    // books property in AuthorEntity class maps to BookEntity
    // In a bi-directional association, the @ManyToMany annotation is used on both the entities but only one entity can
    // be the owner of the relationship. The entity that specifies the mappedBy attribute is the inverse side.
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "books"
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<AuthorEntity> authors = new HashSet<>();

    public BookEntity() {
    }

    // We do not set any mapping fields in the constructor
    public BookEntity(String isbn, String title, int yearPublished, String edition) {
        this.isbn = isbn;
        this.title = title;
        this.yearPublished = yearPublished;
        this.edition = edition;
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

    public int getYearPublished() {
        return yearPublished;
    }

    public String getEdition() {
        return edition;
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

    public PublisherEntity getPublisher() {
        return publisher;
    }

    public void setPublisher(PublisherEntity publisher) {
        this.publisher = publisher;
    }
}

