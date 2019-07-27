package com.skb.course.apis.libraryapis.exception;

import com.skb.course.apis.libraryapis.model.LibraryApiError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class LibraryControllersExceptionHandler extends ResponseEntityExceptionHandler {

    private static Logger logger = LoggerFactory.getLogger(LibraryControllersExceptionHandler.class);

    @ExceptionHandler(LibraryResourceNotFoundException.class)
    public final ResponseEntity<LibraryApiError> handleLibraryResourceNotFoundException(
            LibraryResourceNotFoundException e, WebRequest webRequest) {
        return new ResponseEntity<>(new LibraryApiError(e.getTraceId(), e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(LibraryResourceUnauthorizedException.class)
    public final ResponseEntity<LibraryApiError> handleLibraryResourceUnauthorizedException(
            LibraryResourceUnauthorizedException e, WebRequest webRequest) {
        return new ResponseEntity<>(new LibraryApiError(e.getTraceId(), e.getMessage()),
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(LibraryResourceBadRequestException.class)
    public final ResponseEntity<LibraryApiError> handleLibraryResourceBadRequestException(
            LibraryResourceBadRequestException e, WebRequest webRequest) {
        return new ResponseEntity<>(new LibraryApiError(e.getTraceId(), e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LibraryResourceAlreadyExistException.class)
    public final ResponseEntity<LibraryApiError> handleLibraryResourceAlreadyExistException(
            LibraryResourceAlreadyExistException e, WebRequest webRequest) {

        return new ResponseEntity<>(new LibraryApiError(e.getTraceId(), e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BookNotIssuedException.class)
    public final ResponseEntity<LibraryApiError> handleBookNotIssuedException(
            BookNotIssuedException e, WebRequest webRequest) {

        return new ResponseEntity<>(new LibraryApiError(e.getTraceId(), e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<LibraryApiError> handleAllExceptions(
            Exception e, WebRequest webRequest) {
        logger.error(webRequest.getHeader("Trace-Id"), e);
        return new ResponseEntity<>(new LibraryApiError(webRequest.getHeader("Trace-Id"),
                "Server encountered an error. Please contact support."), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
