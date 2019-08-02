package com.skb.course.apis.libraryapis.repository;

import com.skb.course.apis.libraryapis.entity.PublisherEntity;
import com.skb.course.apis.libraryapis.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PublisherRepository extends CrudRepository<PublisherEntity, Integer> {

    List<PublisherEntity> findByNameContaining(String name);
}
