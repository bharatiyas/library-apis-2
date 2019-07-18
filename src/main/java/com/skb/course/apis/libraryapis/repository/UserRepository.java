package com.skb.course.apis.libraryapis.repository;

import com.skb.course.apis.libraryapis.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
//import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<UserEntity, Integer> {
    UserEntity findByUsername(String username);

    // Enabling ignoring case for all suitable properties
    List<UserEntity> findByLastNameAndFirstName(String lastName, String firstName);

    List<UserEntity> findByFirstName(String firstName);

    List<UserEntity> findByLastName(String lastName);
}
