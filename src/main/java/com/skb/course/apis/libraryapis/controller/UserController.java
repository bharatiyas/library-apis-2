package com.skb.course.apis.libraryapis.controller;

import com.skb.course.apis.libraryapis.exception.*;
import com.skb.course.apis.libraryapis.model.Book;
import com.skb.course.apis.libraryapis.model.IssueBookResponse;
import com.skb.course.apis.libraryapis.model.LibraryApiError;
import com.skb.course.apis.libraryapis.model.LibraryUser;
import com.skb.course.apis.libraryapis.service.UserService;
import com.skb.course.apis.libraryapis.util.LibraryApiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping(path="/users")
public class UserController {

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/register")
    public ResponseEntity<?> registerUser(@RequestBody LibraryUser libraryUser,
                                          @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId)
            throws LibraryResourceAlreadyExistException {
        if(!LibraryApiUtils.doesStringValueExist(traceId)) {
            traceId = UUID.randomUUID().toString();
        }

        libraryUser = userService.addUser(libraryUser, traceId);
        return new ResponseEntity<>(libraryUser, HttpStatus.CREATED);
    }

    @GetMapping(path = "/{userId}")
    public ResponseEntity<?> getUser(@PathVariable int userId, @RequestHeader("Authorization") String bearerToken,
                                     @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId)
            throws LibraryResourceNotFoundException, LibraryResourceUnauthorizedException {

        if(!LibraryApiUtils.doesStringValueExist(traceId)) {
            traceId = UUID.randomUUID().toString();
        }

        int userIdFromClaim = LibraryApiUtils.getUserIdFromClaim(bearerToken);
        String roleFromClaim = LibraryApiUtils.getRoleFromClaim(bearerToken);
        if(roleFromClaim.equals("USER") && userId != userIdFromClaim)   {
            // Logging UserId for security audit trail.
            logger.error(traceId +  userIdFromClaim +
                    " attempted to get the details of userId: " + userId + ". Disallowed.");
            throw new LibraryResourceUnauthorizedException(traceId, "You cannot get the details of userId: " + userId);
        }
        LibraryUser libraryUser = null;
        try {
            libraryUser = userService.getUserByUserId(userId, traceId);
        } catch (LibraryResourceNotFoundException e) {
            logger.error(traceId + e.getMessage());
            throw e;
        }
        return new ResponseEntity<>(libraryUser, HttpStatus.OK);
    }

    // This method can be used to update Password, PhoneNumber and EmailId
    // You cannot update any other details for a user
    @PutMapping(path = "/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable int userId, @RequestBody LibraryUser libraryUser,
                                        @RequestHeader("Authorization") String bearerToken,
                                        @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId)
            throws LibraryResourceUnauthorizedException, LibraryResourceBadRequestException, LibraryResourceNotFoundException {
        int userIdFromClaim = LibraryApiUtils.getUserIdFromClaim(bearerToken);
        String roleFromClaim = LibraryApiUtils.getRoleFromClaim(bearerToken);

        if(!LibraryApiUtils.doesStringValueExist(traceId)) {
            traceId = UUID.randomUUID().toString();
        }
        // Even if you are admin you cannot update a User
        if(LibraryApiUtils.isUserAdmin(bearerToken)) {
            // Logging UserId for security audit trail.
            logger.error(traceId +  userIdFromClaim +
                    " (admin) attempted to update userId: " + userId + ". Disallowed.");
            throw new LibraryResourceUnauthorizedException(traceId, "You (admin) cannot update a User: " + userId);
        }

        // Check for User validity. A User can update ONLY its details, not anyone else's
        if(roleFromClaim.equals("USER") && (userId != userIdFromClaim))   {
            // Logging UserId for security audit trail.
            logger.error(traceId +  userIdFromClaim +
                    " attempted to update details for userId: " + userId + ". Disallowed.");
            throw new LibraryResourceUnauthorizedException(traceId, "You cannot update details for userId: " + userId);
        }
        if((libraryUser.getUserId() != null) && (libraryUser.getUserId() != userId)) {
            logger.error(traceId + " Invalid User Id. User Id in the request and URL do not match.");
            throw new LibraryResourceBadRequestException(traceId, "Invalid User Id. User Id in the request and URL do not match.");
        }

        // Sanity check done. You are good to go.
        try {
            libraryUser.setUserId(userIdFromClaim);
            libraryUser = userService.updateUser(libraryUser, traceId);
        } catch (LibraryResourceNotFoundException e) {
            logger.error(traceId + e.getMessage());
            throw e;
        }
        return new ResponseEntity<>(libraryUser, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable int userId, @RequestHeader("Authorization") String bearerToken,
                                        @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId)
            throws LibraryResourceUnauthorizedException, LibraryResourceNotFoundException {

        int userIdFromClaim = LibraryApiUtils.getUserIdFromClaim(bearerToken);
        String roleFromClaim = LibraryApiUtils.getRoleFromClaim(bearerToken);

        if(!LibraryApiUtils.doesStringValueExist(traceId)) {
            traceId = UUID.randomUUID().toString();
        }

        // Even if you are admin you cannot delete a User
        if(LibraryApiUtils.isUserAdmin(bearerToken)) {
            // Logging UserId for security audit trail.
            logger.error(traceId +  userIdFromClaim +
                    " (admin) attempted to delete userId: " + userId + ". Disallowed.");
            throw new LibraryResourceUnauthorizedException(traceId, "You (admin) cannot delete User: " + userId);
        }

        // Check for User validity. A User can delete ONLY itself
        if(roleFromClaim.equals("USER") && userId != userIdFromClaim)   {
            // Logging UserId for security audit trail.
            logger.error(traceId +  userIdFromClaim +
                    " attempted to update details for userId: " + userId + ". Disallowed.");
            throw new LibraryResourceUnauthorizedException(traceId, "You cannot delete User with userId: " + userId);
        }

        try {
            userService.deleteUserByUserId(userId, traceId);
        } catch (LibraryResourceNotFoundException e) {
            logger.error(traceId + e.getMessage());
            throw e;
        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping(path = "/search")
    public ResponseEntity<?> searchUsers(@RequestParam String firstName, @RequestParam String lastName,
                                         /*@RequestParam(defaultValue = "0") Integer pageNo,
                                         @RequestParam(defaultValue = "10") Integer pageSize,
                                         @RequestParam(defaultValue = "userId") String sortBy,*/
                                         @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId
    ) throws LibraryResourceNotFoundException {

        if(!LibraryApiUtils.doesStringValueExist(traceId)) {
            traceId = UUID.randomUUID().toString();
        }

        List<LibraryUser> libraryUsers = null;
        try {
            if(!LibraryApiUtils.doesStringValueExist(firstName) && !LibraryApiUtils.doesStringValueExist(lastName)) {
                return new ResponseEntity<>(new LibraryApiError(traceId, "Please enter at least one search criteria"), HttpStatus.BAD_REQUEST);
            }
            libraryUsers = userService.searchUsers(firstName, lastName, traceId);
        } catch (LibraryResourceNotFoundException e) {
            logger.error(traceId + e.getMessage());
            throw e;
        }
        return new ResponseEntity<>(libraryUsers, HttpStatus.OK);
    }

    @PutMapping(path = "/{userId}/books")
    public ResponseEntity<?> issueBooks(@PathVariable int userId, @RequestBody Set<Integer> bookIds,
                                           @RequestHeader("Authorization") String bearerToken,
                                           @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId)
            throws LibraryResourceUnauthorizedException, LibraryResourceBadRequestException, LibraryResourceNotFoundException {
        if(!LibraryApiUtils.doesStringValueExist(traceId)) {
            traceId = UUID.randomUUID().toString();
        }
        if(!LibraryApiUtils.isUserAdmin(bearerToken)) {
            // Logging UserId for security audit trail.
            logger.error(traceId +  LibraryApiUtils.getUserIdFromClaim(bearerToken) + " attempted to issue Books. Disallowed. " +
                    "User is not a Admin.");
            throw new LibraryResourceUnauthorizedException(traceId, " attempted to issue Books. Disallowed.");
        }
        if(bookIds == null || bookIds.size() == 0) {
            logger.error(traceId + " Invalid Book list. List is either not present or empty.");
            throw new LibraryResourceBadRequestException(traceId, "Invalid Book list. List is either not present or empty.");
        }
        IssueBookResponse issueBookResponse = null;
        try {
            issueBookResponse = userService.issueBooks(userId, bookIds, traceId);
        } catch (LibraryResourceNotFoundException e) {
            logger.error(traceId + e.getMessage());
            throw e;
        }
        return new ResponseEntity<>(issueBookResponse, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{userId}/books/{bookId}")
    public ResponseEntity<?> returnBooks(@PathVariable int userId, @PathVariable int bookId,
                                         @RequestHeader("Authorization") String bearerToken,
                                         @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId)
            throws LibraryResourceUnauthorizedException, LibraryResourceBadRequestException, LibraryResourceNotFoundException {
        if(!LibraryApiUtils.doesStringValueExist(traceId)) {
            traceId = UUID.randomUUID().toString();
        }
        if(!LibraryApiUtils.isUserAdmin(bearerToken)) {
            // Logging UserId for security audit trail.
            logger.error(traceId +  LibraryApiUtils.getUserIdFromClaim(bearerToken) + " attempted to return Books. Disallowed. " +
                    "User is not a Admin.");
            throw new LibraryResourceUnauthorizedException(traceId, " attempted to delete Books. Disallowed.");
        }
        try {
            userService.returnBooks(userId, bookId, traceId);
        } catch (LibraryResourceNotFoundException e) {
            logger.error(traceId + e.getMessage());
            throw e;
        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
