package com.skb.course.apis.libraryapis.controller;

import com.skb.course.apis.libraryapis.exception.*;
import com.skb.course.apis.libraryapis.model.Book;
import com.skb.course.apis.libraryapis.model.LibraryApiError;
import com.skb.course.apis.libraryapis.service.BookService;
import com.skb.course.apis.libraryapis.util.LibraryApiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping(path="/books")
public class BookController {

    private static Logger logger = LoggerFactory.getLogger(BookController.class);

    private BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    private ResponseEntity<Book> addBook(@RequestBody Book book, @RequestHeader("Authorization") String bearerToken,
                                      @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId)
            throws LibraryResourceUnauthorizedException, LibraryResourceNotFoundException, LibraryResourceAlreadyExistException {
        if(!LibraryApiUtils.doesStringValueExist(traceId)) {
            traceId = UUID.randomUUID().toString();
        }
        if(!LibraryApiUtils.isUserAdmin(bearerToken)) {
            // Logging UserId for security audit trail.
            logger.error(traceId +  LibraryApiUtils.getUserIdFromClaim(bearerToken) + " attempted to add an Author. Disallowed. " +
                    "User is not a Admin.");
            throw new LibraryResourceUnauthorizedException(traceId, "You cannot update Author details");
        }
        try {
            book = bookService.addBook(book, traceId);
        } catch (DataIntegrityViolationException e) {
            throw new LibraryResourceAlreadyExistException(traceId, "Book already exists!! Please verify the ISBN.");
        }
        return new ResponseEntity<>(book, HttpStatus.CREATED);
    }

    @GetMapping(path = "/{bookId}")
    public ResponseEntity<Book> getBook(@PathVariable int bookId,
                                        @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId)
            throws LibraryResourceNotFoundException {
        if(!LibraryApiUtils.doesStringValueExist(traceId)) {
            traceId = UUID.randomUUID().toString();
        }

        Book book = null;
        try {
            book = bookService.getBook(bookId, traceId);
        } catch (LibraryResourceNotFoundException e) {
            logger.error(traceId + e.getMessage());
            throw e;
        }
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    @PutMapping(path = "/{bookId}")
    public ResponseEntity<Book> updateBook(@PathVariable int bookId, @RequestBody Book book,
                                        @RequestHeader("Authorization") String bearerToken,
                                        @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId)
            throws LibraryResourceUnauthorizedException, LibraryResourceBadRequestException, LibraryResourceNotFoundException {
        if(!LibraryApiUtils.doesStringValueExist(traceId)) {
            traceId = UUID.randomUUID().toString();
        }
        if(!LibraryApiUtils.isUserAdmin(bearerToken)) {
            // Logging UserId for security audit trail.
            logger.error(traceId +  LibraryApiUtils.getUserIdFromClaim(bearerToken) + " attempted to update an Book. Disallowed. " +
                    "User is not a Admin.");
            throw new LibraryResourceUnauthorizedException(traceId, "You cannot update Book details.");
        }
        if((book.getBookId() != null) && (book.getBookId() != bookId)) {
            logger.error(traceId + " Invalid Book Id. Book Id in the request and URL do not match.");
            throw new LibraryResourceBadRequestException(traceId, "Invalid Book Id. Book Id in the request and URL do not match.");
        }
        try {
            book.setBookId(bookId);
            book = bookService.updateBook(book, traceId);
        } catch (LibraryResourceNotFoundException e) {
            logger.error(traceId + e.getMessage());
            throw e;
        }
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{bookId}")
    public ResponseEntity<?> deleteBook(@PathVariable int bookId, @RequestHeader("Authorization") String bearerToken,
                                          @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId)
            throws LibraryResourceUnauthorizedException, LibraryResourceNotFoundException {
        if(!LibraryApiUtils.doesStringValueExist(traceId)) {
            traceId = UUID.randomUUID().toString();
        }
        if(!LibraryApiUtils.isUserAdmin(bearerToken)) {
            // Logging UserId for security audit trail.
            logger.error(traceId +  LibraryApiUtils.getUserIdFromClaim(bearerToken) + " attempted to delete an Book. Disallowed. " +
                    "User is not a Admin.");
            throw new LibraryResourceUnauthorizedException(traceId, "You cannot delete an Book.");
        }
        try {
            bookService.deleteAuthor(bookId, traceId);
        } catch (LibraryResourceNotFoundException e) {
            logger.error(traceId + e.getMessage());
            throw e;
        }

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PutMapping(path = "/{bookId}/authors")
    public ResponseEntity<?> addBookAuhors(@PathVariable int bookId, @RequestBody Set<Integer> authorIds,
                                           @RequestHeader("Authorization") String bearerToken,
                                           @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId)
            throws LibraryResourceUnauthorizedException, LibraryResourceBadRequestException, LibraryResourceNotFoundException {
        if(!LibraryApiUtils.doesStringValueExist(traceId)) {
            traceId = UUID.randomUUID().toString();
        }
        if(!LibraryApiUtils.isUserAdmin(bearerToken)) {
            // Logging UserId for security audit trail.
            logger.error(traceId +  LibraryApiUtils.getUserIdFromClaim(bearerToken) + " attempted add Authors to the Book. Disallowed. " +
                    "User is not a Admin.");
            throw new LibraryResourceUnauthorizedException(traceId, "You cannot add Authors to the Book.");
        }
        if(authorIds == null || authorIds.size() == 0) {
            logger.error(traceId + " Invalid Authors list. List is either not present or empty.");
            throw new LibraryResourceBadRequestException(traceId, "Invalid Authors list. List is either not present or empty.");
        }
        Book book = null;
        try {
            book = bookService.addBookAuhors(bookId, authorIds, traceId);
        } catch (LibraryResourceNotFoundException e) {
            logger.error(traceId + e.getMessage());
            throw e;
        }
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

}
