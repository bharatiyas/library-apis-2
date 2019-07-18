package com.skb.course.apis.libraryapis.controller;

import com.skb.course.apis.libraryapis.exception.PublisherNotFoundException;
import com.skb.course.apis.libraryapis.model.Publisher;
import com.skb.course.apis.libraryapis.service.PublisherService;
import com.skb.course.apis.libraryapis.service.PublisherService;
import com.skb.course.apis.libraryapis.util.LibraryApiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/publishers")
public class PublisherController {

    private static Logger logger = LoggerFactory.getLogger(PublisherController.class);

    @Autowired
    private PublisherService publisherService;

    @PostMapping(path = "/")
    public ResponseEntity<?> addPublisher(@RequestBody Publisher publisher,
                                          @RequestHeader("Authorization") String bearerToken) {
        if(!LibraryApiUtils.isUserAdmin(bearerToken)) {
            return new ResponseEntity<>("You cannot add a Publisher", HttpStatus.UNAUTHORIZED);
        }
        try {
            publisher = publisherService.addPublisher(publisher);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(publisher, HttpStatus.CREATED);
    }

    @GetMapping(path = "/{publisherId}")
    public ResponseEntity<?> getPublisher(@PathVariable int publisherId) {

        Publisher publisher = null;
        try {
            publisher = publisherService.getPublisher(publisherId);
        } catch (PublisherNotFoundException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(publisher, HttpStatus.OK);
    }

    @PutMapping(path = "/{publisherId}")
    public ResponseEntity<?> updatePublisher(@PathVariable int publisherId, @RequestBody Publisher publisher,
                                             @RequestHeader("Authorization") String bearerToken) {
        if(!LibraryApiUtils.isUserAdmin(bearerToken)) {
            return new ResponseEntity<>("You cannot update Publisher details", HttpStatus.UNAUTHORIZED);
        }
        if(((publisher.getPublisherId() != null) ) && (publisher.getPublisherId() != publisherId)) {
            return new ResponseEntity<>("Invalid Publisher Id", HttpStatus.BAD_REQUEST);
        }
        try {
            publisher = publisherService.updatePublisher(publisher);
        } catch (PublisherNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(publisher, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{publisherID}")
    public ResponseEntity<?> deletePublisher(@PathVariable int publisherId, @RequestHeader("Authorization") String bearerToken) {
        if(!LibraryApiUtils.isUserAdmin(bearerToken)) {
            return new ResponseEntity<>("You cannot delete an publisher", HttpStatus.UNAUTHORIZED);
        }
        try {
            publisherService.deletePublisher(publisherId);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
