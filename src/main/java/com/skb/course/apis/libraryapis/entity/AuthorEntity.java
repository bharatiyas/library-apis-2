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

    // BookEntity has M-M relationship with AuthorEntity
    // authors property in BookEntity class maps to AuthorEntity
    // In a bi-directional association, the @ManyToMany annotation is used on both the entities but only one entity can
    // be the owner of the relationship. The entity that specifies the mappedBy attribute is the inverse side.
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            mappedBy = "authors"
    )
    //@OnDelete(action = OnDeleteAction.CASCADE)
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

