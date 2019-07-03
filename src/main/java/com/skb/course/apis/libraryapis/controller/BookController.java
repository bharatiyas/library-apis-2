package com.skb.course.apis.libraryapis.controller;

import com.skb.course.apis.libraryapis.model.Book;
import com.skb.course.apis.libraryapis.model.User;
import com.skb.course.apis.libraryapis.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path="/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping(path = "/")
    private ResponseEntity<?> addBook(@RequestBody Book book) {
        return new ResponseEntity<>(bookService.addBook(book), HttpStatus.CREATED);
    }
}
