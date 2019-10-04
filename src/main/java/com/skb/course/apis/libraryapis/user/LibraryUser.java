package com.skb.course.apis.libraryapis.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.skb.course.apis.libraryapis.book.Book;
import com.skb.course.apis.libraryapis.model.common.Gender;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.Email;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

// Spring Security will use the information stored in the LibraryUser object to perform authentication and authorization.
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LibraryUser implements UserDetails {

    private Integer userId;

    @Size(min = 1, max = 50, message
            = "Username must be between 1 and 50 characters")
    private String username;

    @Size(min = 8, max = 20, message
            = "Password must be between 8 and 20 characters")
    private String password;

    @Size(min = 1, max = 50, message
            = "First Name must be between 1 and 50 characters")
    private String firstName;

    @Size(min = 1, max = 50, message
            = "Last Name must be between 1 and 50 characters")
    private String lastName;

    @Past(message = "Date of birth must be a past date")
    private LocalDate dateOfBirth;

    private Gender gender;

    @Pattern(regexp = "\\d{3}-\\d{3}-\\d{3}", message = "Please enter phone number in format 123-456-789")
    private String phoneNumber;

    @Email(message = "Please enter a valid EmailId")
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
