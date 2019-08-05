package com.skb.course.apis.libraryapis.repository;

import com.skb.course.apis.libraryapis.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
//import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<UserEntity, Integer> {
    UserEntity findByUsername(String username);

    List<UserEntity> findByFirstNameAndLastNameContaining(String firstName, String lastName);

    List<UserEntity> findByFirstNameContaining(String firstName);

    List<UserEntity> findByLastNameContaining(String lastName);
}
