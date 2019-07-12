package com.skb.course.apis.libraryapis.service;

import com.skb.course.apis.libraryapis.entity.PublisherEntity;
import com.skb.course.apis.libraryapis.exception.PublisherNotFoundException;
import com.skb.course.apis.libraryapis.model.Publisher;
import com.skb.course.apis.libraryapis.repository.PublisherRepository;
import com.skb.course.apis.libraryapis.util.LibraryApiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PublisherService {

    @Autowired
    private PublisherRepository publisherRepository;

    public Publisher addPublisher(Publisher publisherToBeAdded) {
        PublisherEntity bookEntity = new PublisherEntity(
                publisherToBeAdded.getName(),
                publisherToBeAdded.getEmailId(),
                publisherToBeAdded.getPhoneNumber());

        PublisherEntity addedPublisher = publisherRepository.save(bookEntity);

        publisherToBeAdded.setPublisherId(addedPublisher.getPublisherId());
        return publisherToBeAdded;
    }

    public Publisher getPublisher(int publisherId) throws PublisherNotFoundException {
        Optional<PublisherEntity> publisherEntity = publisherRepository.findById(publisherId);
        Publisher publisher = null;
        if(publisherEntity.isPresent()) {
            PublisherEntity ue = publisherEntity.get();
            publisher = createPublisherFromEntity(ue);
        } else {
            throw new PublisherNotFoundException("Publisher Id: " + publisherId + " Not Found");
        }
        return publisher;
    }

    public Publisher updatePublisher(Publisher publisherToBeUpdated) throws PublisherNotFoundException {
        Optional<PublisherEntity> publisherEntity = publisherRepository.findById(publisherToBeUpdated.getPublisherId());
        Publisher publisher = null;
        if(publisherEntity.isPresent()) {
            PublisherEntity pe = publisherEntity.get();
            if(LibraryApiUtils.doesStringValueExist(publisherToBeUpdated.getEmailId())) {
                pe.setEmailId(publisherToBeUpdated.getEmailId());
            }
            if(LibraryApiUtils.doesStringValueExist(publisherToBeUpdated.getPhoneNumber())) {
                pe.setPhoneNumber(publisherToBeUpdated.getPhoneNumber());
            }
            publisherRepository.save(pe);
            publisher = createPublisherFromEntity(pe);
        } else {
            throw new PublisherNotFoundException("Publisher Id: " + publisherToBeUpdated.getPublisherId() + " Not Found");
        }
        return publisher;
    }

    public void deletePublisher(int authorId) {
        publisherRepository.deleteById(authorId);
    }

    private Publisher createPublisherFromEntity(PublisherEntity pe) {
        return new Publisher(pe.getPublisherId(), pe.getName(), pe.getEmailId(), pe.getPhoneNumber());
    }
}
