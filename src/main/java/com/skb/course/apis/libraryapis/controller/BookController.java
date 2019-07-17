package com.skb.course.apis.libraryapis.controller;

import com.skb.course.apis.libraryapis.exception.BookNotFoundException;
import com.skb.course.apis.libraryapis.model.Book;
import com.skb.course.apis.libraryapis.service.BookService;
import com.skb.course.apis.libraryapis.util.LibraryApiUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping(path="/books")
public class BookController {

    private BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping(path = "/")
    private ResponseEntity<?> addBook(@RequestBody Book book, @RequestHeader("Authorization") String bearerToken) {
        if(!LibraryApiUtils.isUserAdmin(bearerToken)) {
            return new ResponseEntity<>("You cannot add a Book", HttpStatus.UNAUTHORIZED);
        }
        try {
            book = bookService.addBook(book);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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
    public ResponseEntity<?> updateBook(@PathVariable int bookId, @RequestBody Book book,
                                        @RequestHeader("Authorization") String bearerToken) {
        if(!LibraryApiUtils.isUserAdmin(bearerToken)) {
            return new ResponseEntity<>("You cannot update the Book details", HttpStatus.UNAUTHORIZED);
        }
        if((book.getBookId() != null) && (book.getBookId() != bookId)) {
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
    public ResponseEntity<?> addBookAuhors(@PathVariable int bookId, @RequestBody Set<Integer> authorIds,
                                           @RequestHeader("Authorization") String bearerToken) {
        if(!LibraryApiUtils.isUserAdmin(bearerToken)) {
            return new ResponseEntity<>("You cannot add Authors to the Book", HttpStatus.UNAUTHORIZED);
        }
        if(authorIds == null || authorIds.size() == 0) {
            throw new IllegalArgumentException("Invalid Authors list");
        }
        Book book = null;
        try {
            book = bookService.addBookAuhors(bookId, authorIds);
        } catch (BookNotFoundException e) {
            return new ResponseEntity<>("Book Not Found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(book, HttpStatus.OK);
    }
}
