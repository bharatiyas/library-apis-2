package com.skb.course.apis.libraryapis.service;

import com.skb.course.apis.libraryapis.entity.PublisherEntity;
import com.skb.course.apis.libraryapis.exception.PublisherNotFoundException;
import com.skb.course.apis.libraryapis.model.Author;
import com.skb.course.apis.libraryapis.model.Publisher;
import com.skb.course.apis.libraryapis.repository.PublisherRepository;
import com.skb.course.apis.libraryapis.util.LibraryApiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public List<Publisher> searchPublishers(String name, Integer pageNo, Integer pageSize,
                                      String sortBy) throws PublisherNotFoundException {
        //Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        List<PublisherEntity> publisherEntities = null;
        if(LibraryApiUtils.doesStringValueExist(name)) {
            publisherEntities = publisherRepository.findByName(name);
        }
        if(publisherEntities != null && publisherEntities.size() > 0) {
            return createPublishersForSearchResponse(publisherEntities);
        } else {
            throw new PublisherNotFoundException("No Publishers found with Name: " + name);
        }
    }

    private Publisher createPublisherFromEntity(PublisherEntity pe) {
        return new Publisher(pe.getPublisherId(), pe.getName(), pe.getEmailId(), pe.getPhoneNumber());
    }

    private List<Publisher> createPublishersForSearchResponse(List<PublisherEntity> publisherEntities) {
        return publisherEntities.stream()
                .map(pe -> new Publisher(pe.getPublisherId(), pe.getName(), pe.getEmailId(), pe.getPhoneNumber()))
                .collect(Collectors.toList());
    }
}
