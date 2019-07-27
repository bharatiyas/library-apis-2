package com.skb.course.apis.libraryapis.exception;

public class BookNotIssuedException extends Exception {

    private String traceId;

    public BookNotIssuedException(String traceId, String message) {

        super(message);
        this.traceId = traceId;
    }

    public String getTraceId() {
        return traceId;
    }
}
