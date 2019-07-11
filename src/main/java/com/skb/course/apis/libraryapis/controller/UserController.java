package com.skb.course.apis.libraryapis.controller;

import com.skb.course.apis.libraryapis.exception.UserNotFoundException;
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
    public ResponseEntity<?> addUser(@RequestBody User user) {
        try {
            user = userService.addUser(user);
        } catch (Exception e) {
            new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @GetMapping(path = "/{userId}")
    public ResponseEntity<?> getUser(@PathVariable int userId) {

        User user = null;
        try {
            user = userService.getUser(userId);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>("User Not Found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping(path = "/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable int userId, @RequestBody User user) {
        if(user.getUserId() != userId) {
            return new ResponseEntity<>("Invalid User Id", HttpStatus.BAD_REQUEST);
        }
        try {
            user = userService.updateUser(user);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>("User Not Found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


}
