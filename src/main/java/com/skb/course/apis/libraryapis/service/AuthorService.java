package com.skb.course.apis.libraryapis.service;

import com.skb.course.apis.libraryapis.entity.AuthorEntity;
import com.skb.course.apis.libraryapis.model.Author;
import com.skb.course.apis.libraryapis.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorService {

    @Autowired
    AuthorRepository authorRepository;

    public Author addAuthor(Author authorToBeAdded) {
        AuthorEntity authorEntity = new AuthorEntity(
                authorToBeAdded.getFirstName(),
                authorToBeAdded.getLastName(),
                authorToBeAdded.getDateOfBirth(),
                authorToBeAdded.getGender());

        AuthorEntity addedAuthor = authorRepository.save(authorEntity);
        authorToBeAdded.setAuthorId(addedAuthor.getAuthorId());

        return authorToBeAdded;
    }
}
