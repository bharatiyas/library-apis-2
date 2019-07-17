package com.skb.course.apis.libraryapis.controller;

import com.skb.course.apis.libraryapis.exception.UserNotFoundException;
import com.skb.course.apis.libraryapis.model.LibraryUser;
import com.skb.course.apis.libraryapis.service.UserService;
import com.skb.course.apis.libraryapis.util.LibraryApiUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    // This method can be used to update Password, PhoneNumber and EmailId
    // You cannot update any other details for a user
    @PutMapping(path = "/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable int userId, @RequestBody LibraryUser libraryUser,
                                        @RequestHeader("Authorization") String bearerToken) {
        int userIdFromClaim = LibraryApiUtils.getUserIdFromClaim(bearerToken);
        String roleFromClaim = LibraryApiUtils.getRoleFromClaim(bearerToken);

        // Even if you are admin you cannot update a User
        if(LibraryApiUtils.isUserAdmin(bearerToken)) {
            return new ResponseEntity<>("You cannot update a User", HttpStatus.UNAUTHORIZED);
        }

        // Check for User validity. A User can update ONLY its details, not anyone else's
        if(roleFromClaim.equals("USER") && (userId != userIdFromClaim))   {
            return new ResponseEntity<>("You cannot update details for userId: " + userId,
                    HttpStatus.UNAUTHORIZED);
        }
        if((libraryUser.getUserId() != null) && (libraryUser.getUserId() != userId)) {
            return new ResponseEntity<>("Invalid LibraryUser Id", HttpStatus.BAD_REQUEST);
        }

        // Sanity check done. You are good to go.
        try {
            libraryUser = userService.updateUser(libraryUser);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>("LibraryUser Not Found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(libraryUser, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable int userId, @RequestHeader("Authorization") String bearerToken) {

        int userIdFromClaim = LibraryApiUtils.getUserIdFromClaim(bearerToken);
        String roleFromClaim = LibraryApiUtils.getRoleFromClaim(bearerToken);

        // Even if you are admin you cannot delete a User
        if(LibraryApiUtils.isUserAdmin(bearerToken)) {
            return new ResponseEntity<>("You cannot delete a User", HttpStatus.UNAUTHORIZED);
        }

        // Check for User validity. A User can delete ONLY itself
        if(roleFromClaim.equals("USER") && userId != userIdFromClaim)   {
            return new ResponseEntity<>("You cannot delete User with userId: " + userId,
                    HttpStatus.UNAUTHORIZED);
        }
        try {
            userService.deleteUserByUserId(userId);
        } catch (Exception e) {
            new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping(path = "/search")
    public ResponseEntity<?> searchUsers(@RequestParam String firstName, @RequestParam String lastName,
                                        @RequestParam(defaultValue = "0") Integer pageNo,
                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                        @RequestParam(defaultValue = "userId") String sortBy
                                        ) {


        List<LibraryUser> libraryUsers = null;
        try {
            libraryUsers = userService.searchUsers(firstName, lastName, pageNo, pageSize, sortBy);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>("LibraryUser Not Found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(libraryUsers, HttpStatus.OK);
    }
}
