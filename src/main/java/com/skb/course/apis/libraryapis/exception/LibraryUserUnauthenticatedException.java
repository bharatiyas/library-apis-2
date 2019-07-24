package com.skb.course.apis.libraryapis.exception;

public class LibraryUserUnauthenticatedException extends Exception {

    private String traceId;

    public LibraryUserUnauthenticatedException(String traceId, String message) {

        super(message);
        this.traceId = traceId;
    }

    public String getTraceId() {
        return traceId;
    }
}
