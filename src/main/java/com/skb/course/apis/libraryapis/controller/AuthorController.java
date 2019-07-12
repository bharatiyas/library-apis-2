package com.skb.course.apis.libraryapis.controller;

import com.skb.course.apis.libraryapis.exception.AuthorNotFoundException;
import com.skb.course.apis.libraryapis.model.Author;
import com.skb.course.apis.libraryapis.model.Book;
import com.skb.course.apis.libraryapis.service.AuthorService;
import com.skb.course.apis.libraryapis.service.BookService;
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
    public ResponseEntity<?> addAuthor(@RequestBody Author author) {
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
    public ResponseEntity<?> updateAuthor(@PathVariable int authorID, @RequestBody Author author) {
        if(author.getAuthorId() != authorID) {
            return new ResponseEntity<>("Invalid LibraryUser Id", HttpStatus.BAD_REQUEST);
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
}
