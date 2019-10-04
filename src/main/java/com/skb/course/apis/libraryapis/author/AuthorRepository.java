package com.skb.course.apis.libraryapis.author;

import com.skb.course.apis.libraryapis.author.AuthorEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AuthorRepository extends CrudRepository<AuthorEntity, Integer> {

    List<AuthorEntity> findByFirstNameContaining(String firstName);

    List<AuthorEntity> findByLastNameContaining(String lastName);

    List<AuthorEntity> findByFirstNameAndLastNameContaining(String firstName, String lastName);
}
