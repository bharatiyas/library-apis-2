package com.skb.course.apis.libraryapis.controller;

import com.skb.course.apis.libraryapis.exception.BookNotFoundException;
import com.skb.course.apis.libraryapis.model.Author;
import com.skb.course.apis.libraryapis.model.Book;
import com.skb.course.apis.libraryapis.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping(path="/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping(path = "/")
    private ResponseEntity<?> addBook(@RequestBody Book book) {
        try {
            book = bookService.addBook(book);
        } catch (Exception e) {
            new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(book, HttpStatus.CREATED);
    }

    @GetMapping(path = "/{bookId}")
    public ResponseEntity<?> getBook(@PathVariable int bookId) {

        Book book = null;
        try {
            book = bookService.getBook(bookId);
        } catch (BookNotFoundException e) {
            return new ResponseEntity<>("Book Not Found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    @PutMapping(path = "/{bookId}")
    public ResponseEntity<?> updateBook(@PathVariable int bookId, @RequestBody Book book) {
        if(book.getBookId() != bookId) {
            return new ResponseEntity<>("Invalid Book Id", HttpStatus.BAD_REQUEST);
        }
        try {
            book = bookService.updateBook(book);
        } catch (BookNotFoundException e) {
            return new ResponseEntity<>("Book Not Found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    @PutMapping(path = "/{bookId}/authors")
    public ResponseEntity<?> addBookAuhors(@PathVariable int bookId, @RequestBody Set<Author> authors) {

        try {
            book = bookService.addBookAuhors(bookId, authors);
        } catch (BookNotFoundException e) {
            return new ResponseEntity<>("Book Not Found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(book, HttpStatus.OK);
    }
}
