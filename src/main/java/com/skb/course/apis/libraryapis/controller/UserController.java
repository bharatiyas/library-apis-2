package com.skb.course.apis.libraryapis.controller;

import com.skb.course.apis.libraryapis.model.User;
import com.skb.course.apis.libraryapis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(path = "/")
    private ResponseEntity<?> addUser(@RequestBody User user) {
        return new ResponseEntity<>(userService.addUser(user), HttpStatus.CREATED);
    }

    @GetMapping(path = "/{userId}")
    private ResponseEntity<?> getUser(@PathVariable int userId) {

        User user = userService.getUser(userId);
        return user != null ? new ResponseEntity<>(user, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
