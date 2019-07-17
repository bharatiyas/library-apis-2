package com.skb.course.apis.libraryapis.controller;

import com.skb.course.apis.libraryapis.exception.AuthorNotFoundException;
import com.skb.course.apis.libraryapis.model.Author;
import com.skb.course.apis.libraryapis.model.Book;
import com.skb.course.apis.libraryapis.service.AuthorService;
import com.skb.course.apis.libraryapis.service.BookService;
import com.skb.course.apis.libraryapis.util.LibraryApiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/authors")
public class AuthorController {

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
            return new ResponseEntity<>("Author Not Found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
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
            return new ResponseEntity<>("Author Not Found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
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
            new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
