package com.skb.course.apis.libraryapis.repository;

import com.skb.course.apis.libraryapis.entity.AuthorEntity;
import com.skb.course.apis.libraryapis.entity.BookEntity;
import com.skb.course.apis.libraryapis.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AuthorRepository extends CrudRepository<AuthorEntity, Integer> {

    List<AuthorEntity> findByFirstName(String firstName);

    List<AuthorEntity> findByLastName(String lastName);

    List<AuthorEntity> findByLastNameAndFirstName(String lastName, String firstName);
}
