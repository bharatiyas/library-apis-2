package com.skb.course.apis.libraryapis.model;

public enum Gender {
    MALE("Male"),
    FEMALE("Female"),
    INDETERMINATE("Indeterminate");

    private String value;

    Gender(String value) {
        this.value = value;
    }
}
