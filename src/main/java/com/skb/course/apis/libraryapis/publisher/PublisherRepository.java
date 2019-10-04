package com.skb.course.apis.libraryapis.publisher;

import com.skb.course.apis.libraryapis.publisher.PublisherEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PublisherRepository extends CrudRepository<PublisherEntity, Integer> {

    List<PublisherEntity> findByNameContaining(String name);
}
