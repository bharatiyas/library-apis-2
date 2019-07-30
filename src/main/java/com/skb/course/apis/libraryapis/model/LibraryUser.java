package com.skb.course.apis.libraryapis.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

// Spring Security will use the information stored in the LibraryUser object to perform authentication and authorization.
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LibraryUser implements UserDetails {

    private Integer userId;

    private String username;
    private String password;
    private String firstName;
    private String lastName;

    @Past
    private LocalDate dateOfBirth;
    private Gender gender;
    private String phoneNumber;
    private String emailId;

    @JsonIgnore
    private Role role;

    private Set<Book> issuedBooks;

    public LibraryUser() {
    }

    public LibraryUser(int userId, String username, String password, String firstName, String lastName, LocalDate dateOfBirth, Gender gender,
                       String phoneNumber, String emailId, Role role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.emailId = emailId;
        this.role = role;
    }

    public LibraryUser(int userId, String username, String firstName, String lastName, LocalDate dateOfBirth, Gender gender,
                       String phoneNumber, String emailId, Role role) {
        this.userId = userId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.emailId = emailId;
        this.role = role;
    }

    public LibraryUser(String username,String firstName, String lastName, LocalDate dateOfBirth, Gender gender,
                       String phoneNumber, String emailId) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.emailId = emailId;
    }

    public LibraryUser(String username, String firstName, String lastName) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Gender getGender() {
        return gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmailId() {
        return emailId;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Set<Book> getIssuedBooks() {
        return issuedBooks;
    }

    public void setIssuedBooks(Set<Book> issuedBooks) {
        this.issuedBooks = issuedBooks;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "LibraryUser{" +
                "userId='" + userId + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", gender='" + gender + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", emailId='" + emailId + '\'' +
                '}';
    }

}
