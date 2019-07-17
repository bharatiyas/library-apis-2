package com.skb.course.apis.libraryapis.repository;

import com.skb.course.apis.libraryapis.entity.UserEntity;
import com.skb.course.apis.libraryapis.model.LibraryUser;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface UserRepository extends PagingAndSortingRepository<UserEntity, Integer> {
    UserEntity findByUsername(String username);

    // Enabling ignoring case for all suitable properties
    List<UserEntity> findByLastNameAndFirstNameAllIgnoreCase(String lastName, String firstName);
}
