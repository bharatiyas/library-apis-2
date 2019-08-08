package com.skb.course.apis.libraryapis.exception;

import com.skb.course.apis.libraryapis.model.LibraryApiError;
import com.skb.course.apis.libraryapis.util.LibraryApiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.UUID;

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

    @Override
    //@ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String violations = ex.getBindingResult().getFieldErrors().stream()
                .map(errors -> errors.getDefaultMessage().concat("; "))
                .reduce("", String::concat);
        String traceId = getTraceId(request);
        logger.error(traceId, ex);
        return new ResponseEntity<>(new LibraryApiError(traceId, violations), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<LibraryApiError> handleAllExceptions(
            Exception e, WebRequest webRequest) {

        String traceId = getTraceId(webRequest);
        logger.error(traceId, e);
        return new ResponseEntity<>(new LibraryApiError(traceId,
                "Server encountered an error. Please contact support."), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String getTraceId(WebRequest request) {
        String traceId = request.getHeader("Trace-Id");
        if(!LibraryApiUtils.doesStringValueExist(traceId)) {
            traceId = UUID.randomUUID().toString();
        }

        return traceId;
    }
}
