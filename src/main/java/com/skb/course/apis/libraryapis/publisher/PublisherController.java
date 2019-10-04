package com.skb.course.apis.libraryapis.publisher;

import com.skb.course.apis.libraryapis.exception.*;
import com.skb.course.apis.libraryapis.util.LibraryApiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path="/publishers")
public class PublisherController {

    private static Logger logger = LoggerFactory.getLogger(PublisherController.class);

    @Autowired
    private PublisherService publisherService;

    @PostMapping
    public ResponseEntity<Publisher> addPublisher(@RequestBody Publisher publisher,
                                          @RequestHeader("Authorization") String bearerToken,
                                          @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId)
            throws LibraryResourceUnauthorizedException, LibraryResourceAlreadyExistException {
        if(!LibraryApiUtils.doesStringValueExist(traceId)) {
            traceId = UUID.randomUUID().toString();
        }
        if(!LibraryApiUtils.isUserAdmin(bearerToken)) {
            // Logging UserId for security audit trail.
            logger.error(traceId +  LibraryApiUtils.getUserIdFromClaim(bearerToken) + " attempted to add an Publisher. Disallowed. " +
                    "User is not a Admin.");
            throw new LibraryResourceUnauthorizedException(traceId, "You cannot update Publisher details");
        }
        publisher = publisherService.addPublisher(publisher, traceId);

        return new ResponseEntity<>(publisher, HttpStatus.CREATED);
    }

    @GetMapping(path = "/{publisherId}")
    public ResponseEntity<Publisher> getPublisher(@PathVariable int publisherId,
                                                  @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId)
            throws LibraryResourceNotFoundException {
        if(!LibraryApiUtils.doesStringValueExist(traceId)) {
            traceId = UUID.randomUUID().toString();
        }

        Publisher publisher = null;
        try {
            publisher = publisherService.getPublisher(publisherId, traceId);
        } catch (LibraryResourceNotFoundException e) {
            logger.error(traceId + e.getMessage());
            throw e;
        }

        return new ResponseEntity<>(publisher, HttpStatus.OK);
    }

    @PutMapping(path = "/{publisherId}")
    public ResponseEntity<Publisher> updatePublisher(@PathVariable int publisherId, @RequestBody Publisher publisher,
                                             @RequestHeader("Authorization") String bearerToken,
                                             @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId)
            throws LibraryResourceUnauthorizedException, LibraryResourceBadRequestException, LibraryResourceNotFoundException {
        if(!LibraryApiUtils.doesStringValueExist(traceId)) {
            traceId = UUID.randomUUID().toString();
        }
        if(!LibraryApiUtils.isUserAdmin(bearerToken)) {
            // Logging UserId for security audit trail.
            logger.error(traceId +  LibraryApiUtils.getUserIdFromClaim(bearerToken) + " attempted to update an Publisher. Disallowed. " +
                    "User is not a Admin.");
            throw new LibraryResourceUnauthorizedException(traceId, "You cannot update Publisher details.");
        }
        if(((publisher.getPublisherId() != null) ) && (publisher.getPublisherId() != publisherId)) {
            logger.error(traceId + " Invalid Publisher Id. Publisher Id in the request and URL do not match.");
            throw new LibraryResourceBadRequestException(traceId, "Invalid Publisher Id. Publisher Id in the request and URL do not match.");
        }
        try {
            publisher.setPublisherId(publisherId);
            publisher = publisherService.updatePublisher(publisher, traceId);
        } catch (LibraryResourceNotFoundException e) {
            logger.error(traceId + e.getMessage());
            throw e;
        }
        return new ResponseEntity<>(publisher, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{publisherId}")
    public ResponseEntity<?> deletePublisher(@PathVariable int publisherId, @RequestHeader("Authorization") String bearerToken,
                                             @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId)
            throws LibraryResourceUnauthorizedException, LibraryResourceNotFoundException {
        if(!LibraryApiUtils.doesStringValueExist(traceId)) {
            traceId = UUID.randomUUID().toString();
        }
        if(!LibraryApiUtils.isUserAdmin(bearerToken)) {
            // Logging UserId for security audit trail.
            logger.error(traceId +  LibraryApiUtils.getUserIdFromClaim(bearerToken) + " attempted to delete an Publisher. Disallowed. " +
                    "User is not a Admin.");
            throw new LibraryResourceUnauthorizedException(traceId, "You cannot delete an Publisher.");
        }
        try {
            publisherService.deletePublisher(publisherId, traceId);
        } catch (LibraryResourceNotFoundException e) {
            logger.error(traceId + e.getMessage());
            throw e;
        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping(path = "/search")
    public ResponseEntity<?> searchPublishers(@RequestParam String name,
                                           /*@RequestParam(defaultValue = "0") Integer pageNo,
                                           @RequestParam(defaultValue = "10") Integer pageSize,
                                           @RequestParam(defaultValue = "userId") String sortBy,*/
                                              @RequestHeader(value = "Trace-Id", defaultValue = "") String traceId)
            throws LibraryResourceNotFoundException, LibraryResourceBadRequestException {
        if(!LibraryApiUtils.doesStringValueExist(traceId)) {
            traceId = UUID.randomUUID().toString();
        }

        List<Publisher> publishers = null;
        try {
            if(!LibraryApiUtils.doesStringValueExist(name)) {
                logger.error(traceId + " Please enter at least one search criteria to search Publisher.");
                throw new LibraryResourceBadRequestException(traceId, "Please enter at least one search criteria to search Publisher.");
            }
            publishers = publisherService.searchPublishers(name, /*pageNo, pageSize, sortBy, */traceId);
        } catch (LibraryResourceNotFoundException e) {
            logger.error(traceId + ": " + e.getMessage());
            throw e;
        }
        return new ResponseEntity<>(publishers, HttpStatus.OK);
    }
}
