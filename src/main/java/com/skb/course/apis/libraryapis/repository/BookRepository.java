package com.skb.course.apis.libraryapis.repository;

import com.skb.course.apis.libraryapis.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface BookRepository extends CrudRepository<UserEntity, Integer> {
}
