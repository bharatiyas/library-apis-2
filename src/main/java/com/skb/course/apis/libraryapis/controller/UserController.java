package com.skb.course.apis.libraryapis.controller;

import com.skb.course.apis.libraryapis.exception.UserNotFoundException;
import com.skb.course.apis.libraryapis.model.LibraryUser;
import com.skb.course.apis.libraryapis.service.UserService;
import com.skb.course.apis.libraryapis.util.LibraryApiUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/register")
    public ResponseEntity<?> registerUser(@RequestBody LibraryUser libraryUser) {
        try {
            libraryUser = userService.addUser(libraryUser);
        } catch (DataIntegrityViolationException e) {
            if(e.getMessage().contains("constraint [Username]")) {
                return new ResponseEntity<>("Username already exists!! Please use different Username.", HttpStatus.CONFLICT);
            } else {
                return new ResponseEntity<>("EmailId already exists!! You cannot register with same Email address",
                        HttpStatus.CONFLICT);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(libraryUser, HttpStatus.CREATED);
    }

    @GetMapping(path = "/{userId}")
    public ResponseEntity<?> getUser(@PathVariable int userId, @RequestHeader("Authorization") String bearerToken) {

        int userIdFromClaim = LibraryApiUtils.getUserIdFromClaim(bearerToken);
        String roleFromClaim = LibraryApiUtils.getRoleFromClaim(bearerToken);
        if(roleFromClaim.equals("USER") && userId != userIdFromClaim)   {
            return new ResponseEntity<>("You cannot get details for userId: " + userId,
                    HttpStatus.UNAUTHORIZED);
        }
        LibraryUser libraryUser = null;
        try {
            libraryUser = userService.getUserByUserId(userId);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>("LibraryUser Not Found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(libraryUser, HttpStatus.OK);
    }

    @PutMapping(path = "/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable int userId, @RequestBody LibraryUser libraryUser) {
        if(libraryUser.getUserId() != userId) {
            return new ResponseEntity<>("Invalid LibraryUser Id", HttpStatus.BAD_REQUEST);
        }
        try {
            libraryUser = userService.updateUser(libraryUser);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>("LibraryUser Not Found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(libraryUser, HttpStatus.OK);
    }


}
