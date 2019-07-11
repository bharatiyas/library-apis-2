package com.skb.course.apis.libraryapis.entity;

import com.skb.course.apis.libraryapis.model.Gender;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "AUTHOR")
public class AuthorEntity {

    @Column(name = "Author_Id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "authorId_generator")
    @SequenceGenerator(name="authorId_generator", sequenceName = "author_sequence", allocationSize = 50)
    private int authorId;

    @Column(name = "First_Name")
    private String firstName;

    @Column(name = "Last_Name")
    private String lastName;

    @Column(name = "Date_Of_Birth")
    private LocalDate dateOfBirth;

    @Column(name = "Gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    // In a bi-directional association, the @ManyToMany annotation is used on both the entities but only one entity can
    // be the owner of the relationship.
    // FetchType.LAZY - Fetch the related entity lazily from the database.
    @ManyToMany(fetch = FetchType.LAZY,
                cascade = {CascadeType.PERSIST, CascadeType.MERGE}
                )
    // This is to represent to the mapping table named: BOOK_AUTHOR having 2 columns:
    // BOOK_ID (FK to Book) and AUTHOR_ID (FK to Author)
    // joinColumn = Column name of this Table/Entity (Author)
    // inverseJoinColumn = Column name of other Table (Book)
    // The entity that specifies the @JoinTable is the owning side of the relationship.
    @JoinTable(name = "book_author", joinColumns = @JoinColumn(name = "author_id"), inverseJoinColumns = @JoinColumn(name = "book_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<BookEntity> books = new HashSet<>();

    public AuthorEntity() {
    }

    // We do not set any mapping fields in the constructor
    public AuthorEntity(String firstName, String lastName, LocalDate dateOfBirth, Gender gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
    }

    public int getAuthorId() {
        return authorId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

}

