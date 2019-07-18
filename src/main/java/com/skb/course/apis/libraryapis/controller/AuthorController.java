package com.skb.course.apis.libraryapis.controller;

import com.skb.course.apis.libraryapis.exception.AuthorNotFoundException;
import com.skb.course.apis.libraryapis.exception.UserNotFoundException;
import com.skb.course.apis.libraryapis.model.Author;
import com.skb.course.apis.libraryapis.model.Book;
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

@RestController
@RequestMapping(path="/authors")
public class AuthorController {

    private static Logger logger = LoggerFactory.getLogger(AuthorController.class);

    @Autowired
    private AuthorService authorService;

    @PostMapping(path = "/")
    public ResponseEntity<?> addAuthor(@RequestBody Author author, @RequestHeader("Authorization") String bearerToken) {
        if(!LibraryApiUtils.isUserAdmin(bearerToken)) {
            return new ResponseEntity<>("You cannot add an author", HttpStatus.UNAUTHORIZED);
        }
        try {
            author = authorService.addAuthor(author);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(author, HttpStatus.CREATED);
    }

    @GetMapping(path = "/{authorId}")
    public ResponseEntity<?> getAuthor(@PathVariable int authorId) {

        Author author = null;
        try {
            author = authorService.getAuthor(authorId);
        } catch (AuthorNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(author, HttpStatus.OK);
    }

    @PutMapping(path = "/{authorID}")
    public ResponseEntity<?> updateAuthor(@PathVariable int authorID, @RequestBody Author author,
                                          @RequestHeader("Authorization") String bearerToken) {
        if(!LibraryApiUtils.isUserAdmin(bearerToken)) {
            return new ResponseEntity<>("You cannot update Author details", HttpStatus.UNAUTHORIZED);
        }
        if((author.getAuthorId() != null) && (author.getAuthorId() != authorID)) {
            return new ResponseEntity<>("Invalid Author Id", HttpStatus.BAD_REQUEST);
        }
        try {
            author = authorService.updateAuthor(author);
        } catch (AuthorNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(author, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{authorID}")
    public ResponseEntity<?> deleteAuthor(@PathVariable int authorId, @RequestHeader("Authorization") String bearerToken) {
        if(!LibraryApiUtils.isUserAdmin(bearerToken)) {
            return new ResponseEntity<>("You cannot delete an author", HttpStatus.UNAUTHORIZED);
        }
        try {
            authorService.deleteAuthor(authorId);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping(path = "/search")
    public ResponseEntity<?> searchAuthors(@RequestParam String firstName, @RequestParam String lastName,
                                         @RequestParam(defaultValue = "0") Integer pageNo,
                                         @RequestParam(defaultValue = "10") Integer pageSize,
                                         @RequestParam(defaultValue = "userId") String sortBy
    ) {


        List<Author> authors = null;
        try {
            if(!LibraryApiUtils.doesStringValueExist(firstName) && !LibraryApiUtils.doesStringValueExist(lastName)) {
                return new ResponseEntity<>("Please enter at least one search criteria", HttpStatus.BAD_REQUEST);
            }
            authors = authorService.searchAuthors(firstName, lastName, pageNo, pageSize, sortBy);
        } catch (AuthorNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(authors, HttpStatus.OK);
    }
}
