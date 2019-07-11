package com.skb.course.apis.libraryapis.controller;

import com.skb.course.apis.libraryapis.exception.PublisherNotFoundException;
import com.skb.course.apis.libraryapis.model.Publisher;
import com.skb.course.apis.libraryapis.service.PublisherService;
import com.skb.course.apis.libraryapis.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/publishers")
public class PublisherController {

    @Autowired
    private PublisherService publisherService;

    @PostMapping(path = "/")
    public ResponseEntity<?> addPublisher(@RequestBody Publisher publisher) {
        try {
            publisher = publisherService.addPublisher(publisher);
        } catch (Exception e) {
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
            return new ResponseEntity<>("Publisher Not Found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(publisher, HttpStatus.OK);
    }

    @PutMapping(path = "/{userId}")
    public ResponseEntity<?> updatePublisher(@PathVariable int publisherId, @RequestBody Publisher publisher) {
        if(publisher.getPublisherId() != publisherId) {
            return new ResponseEntity<>("Invalid Publisher Id", HttpStatus.BAD_REQUEST);
        }
        try {
            publisher = publisherService.updatePublisher(publisher);
        } catch (PublisherNotFoundException e) {
            return new ResponseEntity<>("Publisher Not Found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(publisher, HttpStatus.OK);
    }
}
