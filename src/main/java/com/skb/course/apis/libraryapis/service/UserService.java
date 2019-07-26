package com.skb.course.apis.libraryapis.service;

import com.skb.course.apis.libraryapis.entity.BookEntity;
import com.skb.course.apis.libraryapis.entity.BookStatusEntity;
import com.skb.course.apis.libraryapis.entity.UserEntity;
import com.skb.course.apis.libraryapis.exception.LibraryResourceNotFoundException;
import com.skb.course.apis.libraryapis.exception.UserNotFoundException;
import com.skb.course.apis.libraryapis.model.*;
import com.skb.course.apis.libraryapis.repository.BookRepository;
import com.skb.course.apis.libraryapis.repository.BookStatusRepository;
import com.skb.course.apis.libraryapis.repository.UserRepository;
import com.skb.course.apis.libraryapis.security.SecurityConstants;
import com.skb.course.apis.libraryapis.util.LibraryApiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserRepository userRepository;
    private BookRepository bookRepository;
    private BookStatusRepository bookStatusRepository;
    private BookService bookService;

    public UserService(BCryptPasswordEncoder bCryptPasswordEncoder, UserRepository userRepository,
                       BookRepository bookRepository, BookStatusRepository bookStatusRepository,
                       BookService bookService) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.bookStatusRepository = bookStatusRepository;
        this.bookService = bookService;
    }

    public LibraryUser addUser(LibraryUser libraryUserToBeAdded) {
        UserEntity userEntity = new UserEntity(
                // Saving password as plain text isn't a good idea therefore encrypt it
                libraryUserToBeAdded.getUsername(),
                bCryptPasswordEncoder.encode(SecurityConstants.NEW_USER_DEFAULT_PASSWORD),
                libraryUserToBeAdded.getFirstName(),
                libraryUserToBeAdded.getLastName(),
                libraryUserToBeAdded.getDateOfBirth(),
                libraryUserToBeAdded.getGender(),
                libraryUserToBeAdded.getPhoneNumber(),
                libraryUserToBeAdded.getEmailId(),
                "USER");

        libraryUserToBeAdded.setPassword(SecurityConstants.NEW_USER_DEFAULT_PASSWORD);
        UserEntity addedUser = userRepository.save(userEntity);
        libraryUserToBeAdded.setUserId(addedUser.getUserId());
        return libraryUserToBeAdded;
    }

    public LibraryUser getUserByUserId(int userId, String traceId) throws LibraryResourceNotFoundException {
        Optional<UserEntity> userEntity = userRepository.findById(userId);
        LibraryUser libraryUser = null;
        if(userEntity.isPresent()) {
            UserEntity ue = userEntity.get();
            libraryUser = createUserFromEntity(ue);
        } else {
            throw new LibraryResourceNotFoundException(traceId, "LibraryUser Id: " + userId + " Not Found");
        }
        return libraryUser;
    }

    public LibraryUser getUserByUsername(String username) throws UserNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(username);
        LibraryUser libraryUser = null;
        if(userEntity != null) {
            libraryUser = createUserFromEntityForLogin(userEntity);
        } else {
            throw new UserNotFoundException("LibraryUsername: " + username + " Not Found");
        }
        return libraryUser;
    }

    public LibraryUser updateUser(LibraryUser libraryUserToBeUpdated, String traceId) throws LibraryResourceNotFoundException {
        Optional<UserEntity> userEntity = userRepository.findById(libraryUserToBeUpdated.getUserId());
        LibraryUser libraryUser = null;
        if(userEntity.isPresent()) {
            UserEntity ue = userEntity.get();
            if(LibraryApiUtils.doesStringValueExist(libraryUserToBeUpdated.getEmailId())) {
                ue.setEmailId(libraryUserToBeUpdated.getEmailId());
            }
            if(LibraryApiUtils.doesStringValueExist(libraryUserToBeUpdated.getPhoneNumber())) {
                ue.setPhoneNumber(libraryUserToBeUpdated.getPhoneNumber());
            }
            if(LibraryApiUtils.doesStringValueExist(libraryUserToBeUpdated.getPassword())) {
                ue.setPassword(bCryptPasswordEncoder.encode(libraryUserToBeUpdated.getPassword()));
            }
            userRepository.save(ue);
            libraryUser = createUserFromEntity(ue);
        } else {
            throw new LibraryResourceNotFoundException(traceId, "LibraryUser Id: " + libraryUserToBeUpdated.getUserId() + " Not Found");
        }
        return libraryUser;
    }

    public void deleteUserByUserId(int userId, String traceId) throws LibraryResourceNotFoundException {
        userRepository.deleteById(userId);
    }

    public List<LibraryUser> searchUsers(String firstName, String lastName, Integer pageNo, Integer pageSize,
                                         String sortBy, String traceId) throws LibraryResourceNotFoundException {
        //Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        List<UserEntity> userEntities = null;
        if(LibraryApiUtils.doesStringValueExist(firstName) && LibraryApiUtils.doesStringValueExist(lastName)) {
            userEntities = userRepository.findByLastNameAndFirstName(lastName, firstName);
        } else if(LibraryApiUtils.doesStringValueExist(firstName) && !LibraryApiUtils.doesStringValueExist(lastName)) {
            userEntities = userRepository.findByFirstName(firstName);
        } else if(!LibraryApiUtils.doesStringValueExist(firstName) && LibraryApiUtils.doesStringValueExist(lastName)) {
            userEntities = userRepository.findByLastName(lastName);
        }
        if(userEntities != null && userEntities.size() > 0) {
            return createUsersForSearchResponse(userEntities);
        } else {
            throw new LibraryResourceNotFoundException(traceId, "No Users found with First name: " + firstName + " and Last name: " + lastName);
        }
    }

    public LibraryUser issueBook(int userId, Set<Integer> bookIds, String traceId) throws LibraryResourceNotFoundException {

        Optional<UserEntity> userEntity = userRepository.findById(userId);
        LibraryUser libraryUser;
        Book book = null;
        if(userEntity.isPresent()) {
            UserEntity ue = userEntity.get();
            Set<BookEntity> books = bookIds.stream()
                    .map(bookId ->
                            bookRepository.findById(bookId)
                    ).collect(Collectors.toSet()).stream()
                    .filter(be -> be.isPresent() == true)
                    .map(be -> be.get())
                    .collect(Collectors.toSet());

            if(books.size() == 0) {
                String booksList = bookIds.stream()
                        .map(bookId -> bookId.toString().concat(" "))
                        .reduce("", String::concat);

                throw new LibraryResourceNotFoundException(traceId, "User Id: " + userId + ". None of the books - " + booksList + " found.");
            }

            Set<BookStatusEntity> booksThatCanBeIssued = books.stream().map(bookEntity -> bookEntity.getBookStatus())
                    .filter(bse -> (bse.getTotalNumberOfCopies() - bse.getNumberOfCopiesIssued()) > 0)
                    .collect(Collectors.toSet());

            if(booksThatCanBeIssued.size() == 0) {
                throw new LibraryResourceNotFoundException(traceId, "User Id: " + userId + ". None of the books could be issued because copies are not available");
            }

            ue.setBooks(books);
            userRepository.save(ue);
            books.stream().map(bookEntity -> bookEntity.getBookStatus())
                    .forEach(bse -> {
                        bse.setTotalNumberOfCopies(bse.getTotalNumberOfCopies() - 1);
                        bse.se
                    });
            libraryUser = createUserFromEntity(ue);
        } else {
            throw new LibraryResourceNotFoundException(traceId, "Library User Id: " + userId + " Not Found");
        }
        return libraryUser;
    }

    private LibraryUser createUserFromEntity(UserEntity ue) {
        LibraryUser libraryUser = new LibraryUser(ue.getUserId(), ue.getUsername(), ue.getFirstName(), ue.getLastName(),
                ue.getDateOfBirth(), ue.getGender(), ue.getPhoneNumber(), ue.getEmailId(), Role.valueOf(ue.getRole()));

        libraryUser.setIssuesBooks(ue.getBooks().stream()
                                        .map(be -> bookService.createBookFromEntity(be))
                                        .collect(Collectors.toSet())
        );

        return libraryUser;
    }

    private LibraryUser createUserFromEntityForLogin(UserEntity ue) {
        return new LibraryUser(ue.getUserId(), ue.getUsername(), ue.getPassword(), ue.getFirstName(), ue.getLastName(),
                ue.getDateOfBirth(), ue.getGender(), ue.getPhoneNumber(), ue.getEmailId(), Role.valueOf(ue.getRole()));
    }

    private List<LibraryUser> createUsersForSearchResponse(List<UserEntity> userEntities) {
        return userEntities.stream()
                .map(ue -> new LibraryUser(ue.getUsername(), ue.getFirstName(), ue.getLastName()))
                .collect(Collectors.toList());
    }

}
