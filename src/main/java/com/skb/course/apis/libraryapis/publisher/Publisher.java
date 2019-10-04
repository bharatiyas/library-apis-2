package com.skb.course.apis.libraryapis.publisher;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Publisher implements Serializable {

    private Integer publisherId;

    @Size(min = 1, max = 50, message
            = "Username must be between 1 and 50 characters")
    private String name;

    @Email(message = "Please enter a valid EmailId")
    private String emailId;

    @Pattern(regexp = "\\d{3}-\\d{3}-\\d{3}", message = "Please enter phone number in format 123-456-789")
    private String phoneNumber;

    public Publisher() {
    }

    public Publisher(int publisherId, String name, String emailId, String phoneNumber) {
        this.publisherId = publisherId;
        this.name = name;
        this.emailId = emailId;
        this.phoneNumber = phoneNumber;
    }

    public Publisher(String name, String emailId, String phoneNumber) {
        this.name = name;
        this.emailId = emailId;
        this.phoneNumber = phoneNumber;
    }

    public Integer getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(int publisherId) {
        this.publisherId = publisherId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
