package com.skb.course.apis.libraryapis.controller;

import com.skb.course.apis.libraryapis.model.User;
import com.skb.course.apis.libraryapis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path="/publishers")
public class PublisherController {

    @Autowired
    private UserService userService;

    @PostMapping(path = "/")
    private ResponseEntity<?> addUser(@RequestBody User user) {
        return new ResponseEntity<>(userService.addUser(user), HttpStatus.CREATED);
    }
}
