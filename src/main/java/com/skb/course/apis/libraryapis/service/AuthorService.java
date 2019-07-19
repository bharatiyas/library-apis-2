package com.skb.course.apis.libraryapis.service;

import com.skb.course.apis.libraryapis.entity.AuthorEntity;
import com.skb.course.apis.libraryapis.exception.AuthorNotFoundException;
import com.skb.course.apis.libraryapis.model.Author;
import com.skb.course.apis.libraryapis.repository.AuthorRepository;
import com.skb.course.apis.libraryapis.util.LibraryApiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public Author getAuthor(int authorId) throws AuthorNotFoundException {
        Optional<AuthorEntity> authorEntity = authorRepository.findById(authorId);
        Author author = null;
        if(authorEntity.isPresent()) {
            AuthorEntity ae = authorEntity.get();
            author = new Author(authorId, ae.getFirstName(), ae.getLastName(),
                    ae.getDateOfBirth(), ae.getGender());
        }  else {
            throw new AuthorNotFoundException("Auhtor Id: " + authorId + " Not Found");
        }
        return author;
    }

    public Author updateAuthor(Author authorToBeUpdated) throws AuthorNotFoundException {
        Optional<AuthorEntity> authorEntity = authorRepository.findById(authorToBeUpdated.getAuthorId());
        Author author = null;
        if(authorEntity.isPresent()) {
            AuthorEntity ue = authorEntity.get();
            if(authorToBeUpdated.getDateOfBirth() != null) {
                ue.setDateOfBirth(authorToBeUpdated.getDateOfBirth());
            }
            if(authorToBeUpdated.getGender() != null) {
                ue.setGender(authorToBeUpdated.getGender());
            }
            authorRepository.save(ue);
            author = createAuthorFromEntity(ue);
        } else {
            throw new AuthorNotFoundException("Auhtor Id: " + authorToBeUpdated.getAuthorId() + " Not Found");
        }
        return author;
    }

    public void deleteAuthor(int authorId) {
        authorRepository.deleteById(authorId);
    }

    public List<Author> searchAuthors(String firstName, String lastName, Integer pageNo, Integer pageSize,
                                      String sortBy) throws AuthorNotFoundException {
        //Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        List<AuthorEntity> authorEntities = null;
        if(LibraryApiUtils.doesStringValueExist(firstName) && LibraryApiUtils.doesStringValueExist(lastName)) {
            authorEntities = authorRepository.findByLastNameAndFirstName(lastName, firstName);
        } else if(LibraryApiUtils.doesStringValueExist(firstName) && !LibraryApiUtils.doesStringValueExist(lastName)) {
            authorEntities = authorRepository.findByFirstName(firstName);
        } else if(!LibraryApiUtils.doesStringValueExist(firstName) && LibraryApiUtils.doesStringValueExist(lastName)) {
            authorEntities = authorRepository.findByLastName(lastName);
        }
        if(authorEntities != null && authorEntities.size() > 0) {
            return createUsersForSearchResponse(authorEntities);
        } else {
            throw new AuthorNotFoundException("No Authors found with First name: " + firstName + " and Last name: " + lastName);
        }
    }

    private Author createAuthorFromEntity(AuthorEntity ae) {
        return new Author(ae.getAuthorId(), ae.getFirstName(), ae.getLastName(),
                ae.getDateOfBirth(), ae.getGender());
    }

    private List<Author> createUsersForSearchResponse(List<AuthorEntity> authorEntities) {
        return authorEntities.stream()
                .map(ae -> new Author(ae.getAuthorId(), ae.getFirstName(), ae.getLastName()))
                .collect(Collectors.toList());
    }
}
