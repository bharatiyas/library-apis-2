package com.skb.course.apis.libraryapis.author;

import com.skb.course.apis.libraryapis.exception.LibraryResourceNotFoundException;
import com.skb.course.apis.libraryapis.util.LibraryApiUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthorService {

    AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Author addAuthor(Author authorToBeAdded, String traceId) {
        AuthorEntity authorEntity = new AuthorEntity(
                authorToBeAdded.getFirstName(),
                authorToBeAdded.getLastName(),
                authorToBeAdded.getDateOfBirth(),
                authorToBeAdded.getGender());

        AuthorEntity addedAuthor = authorRepository.save(authorEntity);
        authorToBeAdded.setAuthorId(addedAuthor.getAuthorId());

        return authorToBeAdded;
    }

    public Author getAuthor(int authorId, String traceId) throws LibraryResourceNotFoundException {
        Optional<AuthorEntity> authorEntity = authorRepository.findById(authorId);
        Author author = null;
        if(authorEntity.isPresent()) {
            AuthorEntity ae = authorEntity.get();
            author = new Author(authorId, ae.getFirstName(), ae.getLastName(),
                    ae.getDateOfBirth(), ae.getGender());
        }  else {
            throw new LibraryResourceNotFoundException(traceId, "Auhtor Id: " + authorId + " Not Found");
        }
        return author;
    }

    public Author updateAuthor(Author authorToBeUpdated, String traceId) throws LibraryResourceNotFoundException {
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
            throw new LibraryResourceNotFoundException(traceId, "Auhtor Id: " + authorToBeUpdated.getAuthorId() + " Not Found");
        }
        return author;
    }

    public void deleteAuthor(int authorId, String traceId) throws LibraryResourceNotFoundException {
        try {
            authorRepository.deleteById(authorId);
        } catch (EmptyResultDataAccessException e) {
            throw new LibraryResourceNotFoundException(traceId, "Auhtor Id: " + authorId + " Not Found");
        }
    }

    public List<Author> searchAuthors(String firstName, String lastName, /*Integer pageNo, Integer pageSize,
                                      String sortBy,*/ String traceId) throws LibraryResourceNotFoundException {
        //Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        List<AuthorEntity> authorEntities = null;
        if(LibraryApiUtils.doesStringValueExist(firstName) && LibraryApiUtils.doesStringValueExist(lastName)) {
            authorEntities = authorRepository.findByFirstNameAndLastNameContaining(firstName, lastName);
        } else if(LibraryApiUtils.doesStringValueExist(firstName) && !LibraryApiUtils.doesStringValueExist(lastName)) {
            authorEntities = authorRepository.findByFirstNameContaining(firstName);
        } else if(!LibraryApiUtils.doesStringValueExist(firstName) && LibraryApiUtils.doesStringValueExist(lastName)) {
            authorEntities = authorRepository.findByLastNameContaining(lastName);
        }
        if(authorEntities != null && authorEntities.size() > 0) {
            return createUsersForSearchResponse(authorEntities);
        } else {
            throw new LibraryResourceNotFoundException(traceId, "No Authors found with First name: " + firstName + " and Last name: " + lastName);
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
