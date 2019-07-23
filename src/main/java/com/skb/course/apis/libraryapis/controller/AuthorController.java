package com.skb.course.apis.libraryapis.controller;

import com.skb.course.apis.libraryapis.exception.*;
import com.skb.course.apis.libraryapis.model.Author;
import com.skb.course.apis.libraryapis.model.Book;
import com.skb.course.apis.libraryapis.model.LibraryApiError;
import com.skb.course.apis.libraryapis.model.LibraryUser;
import com.skb.course.apis.libraryapis.service.AuthorService;
import com.skb.course.apis.libraryapis.service.BookService;
import com.skb.course.apis.libraryapis.util.LibraryApiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path="/authors")
public class AuthorController {

    private static Logger logger = LoggerFactory.getLogger(AuthorController.class);

    @Autowired
    private AuthorService authorService;

    @PostMapping
    public ResponseEntity<Author> addAuthor(@RequestBody Author author, @RequestHeader("Authorization") String bearerToken,
                                       @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId)
            throws LibraryResourceUnauthorizedException {
        if(!LibraryApiUtils.doesStringValueExist(traceId)) {
            traceId = UUID.randomUUID().toString();
        }
        if(!LibraryApiUtils.isUserAdmin(bearerToken)) {
            // Logging UserId for security audit trail.
            logger.error(traceId +  LibraryApiUtils.getUserIdFromClaim(bearerToken) + " attempted to add an Author. Disallowed. " +
                    "User is not a Admin.");
            throw new LibraryResourceUnauthorizedException(traceId, "You cannot update Author details");
        }
        author = authorService.addAuthor(author, traceId);

        return new ResponseEntity<>(author, HttpStatus.CREATED);
    }

    @GetMapping(path = "/{authorId}")
    public ResponseEntity<Author> getAuthor(@PathVariable int authorId,
                                            @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId)
            throws LibraryResourceNotFoundException {
        if(!LibraryApiUtils.doesStringValueExist(traceId)) {
            traceId = UUID.randomUUID().toString();
        }

        Author author = null;
        try {
            author = authorService.getAuthor(authorId, traceId);
        } catch (LibraryResourceNotFoundException e) {
            logger.error(traceId + e.getMessage());
            throw e;
        }
        return new ResponseEntity<>(author, HttpStatus.OK);
    }

    @PutMapping(path = "/{authorID}")
    public ResponseEntity<Author> updateAuthor(@PathVariable int authorID, @RequestBody Author author,
                                          @RequestHeader("Authorization") String bearerToken,
                                          @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId)
            throws LibraryResourceNotFoundException, LibraryResourceBadRequestException, LibraryResourceUnauthorizedException {
        if(!LibraryApiUtils.doesStringValueExist(traceId)) {
            traceId = UUID.randomUUID().toString();
        }
        if(!LibraryApiUtils.isUserAdmin(bearerToken)) {
            // Logging UserId for security audit trail.
            logger.error(traceId +  LibraryApiUtils.getUserIdFromClaim(bearerToken) + " attempted to update an Author. Disallowed. " +
                    "User is not a Admin.");
            throw new LibraryResourceUnauthorizedException(traceId, "You cannot update Author details.");
        }
        if((author.getAuthorId() != null) && (author.getAuthorId() != authorID)) {
            logger.error(traceId + " Invalid Author Id. Author Id in the request and URL do not match.");
            throw new LibraryResourceBadRequestException(traceId, "Invalid Author Id. Author Id in the request and URL do not match.");
        }
        try {
            author = authorService.updateAuthor(author, traceId);
        } catch (LibraryResourceNotFoundException e) {
            logger.error(traceId + e.getMessage());
            throw e;
        }
        return new ResponseEntity<>(author, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{authorId}")
    public ResponseEntity<?> deleteAuthor(@PathVariable int authorId, @RequestHeader("Authorization") String bearerToken,
                                          @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId)
            throws LibraryResourceUnauthorizedException, LibraryResourceNotFoundException {
        if(!LibraryApiUtils.doesStringValueExist(traceId)) {
            traceId = UUID.randomUUID().toString();
        }
        if(!LibraryApiUtils.isUserAdmin(bearerToken)) {
            // Logging UserId for security audit trail.
            logger.error(traceId +  LibraryApiUtils.getUserIdFromClaim(bearerToken) + " attempted to delete an Author. Disallowed. " +
                    "User is not a Admin.");
            throw new LibraryResourceUnauthorizedException(traceId, "You cannot delete an Author.");
        }
        try {
            authorService.deleteAuthor(authorId, traceId);
        } catch (LibraryResourceNotFoundException e) {
            logger.error(traceId + e.getMessage());
            throw e;
        }

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping(path = "/search")
    public ResponseEntity<?> searchAuthors(@RequestParam String firstName, @RequestParam String lastName,
                                         /*@RequestParam(defaultValue = "0") Integer pageNo,
                                         @RequestParam(defaultValue = "10") Integer pageSize,
                                         @RequestParam(defaultValue = "userId") String sortBy,*/
                                         @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId)
            throws LibraryResourceNotFoundException, LibraryResourceBadRequestException {
        if(!LibraryApiUtils.doesStringValueExist(traceId)) {
            traceId = UUID.randomUUID().toString();
        }

        if(!LibraryApiUtils.doesStringValueExist(firstName) && !LibraryApiUtils.doesStringValueExist(lastName)) {
            logger.error(traceId + " Please enter at least one search criteria to search Authors.");
            throw new LibraryResourceBadRequestException(traceId, "Please enter at least one search criteria to search Authors.");
        }
        List<Author> authors = null;
        try {
            authors = authorService.searchAuthors(firstName, lastName, /*pageNo, pageSize, sortBy,*/ traceId);
        } catch (LibraryResourceNotFoundException e) {
            logger.error(traceId + e.getMessage());
            throw e;
        }
        return new ResponseEntity<>(authors, HttpStatus.OK);
    }
}
