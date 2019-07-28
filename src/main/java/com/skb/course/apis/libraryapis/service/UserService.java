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

import java.util.*;
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

    public IssueBookResponse issueBooks(int userId, Set<Integer> bookIds, String traceId) throws LibraryResourceNotFoundException {

        Optional<UserEntity> userEntity = userRepository.findById(userId);

        if(userEntity.isPresent()) {
            Map<Integer, IssueBookStatus> issueBookStatusMap = new HashMap<>(bookIds.size());
            UserEntity ue = userEntity.get();
            Set<BookEntity> issueableBooks = new HashSet<>();
            // Find out if the supplied list of books is issue-able or not
            bookIds.stream()
                    .forEach(bookId -> {
                        Optional<BookEntity> be = bookRepository.findById(bookId);
                        IssueBookStatus bookStatus;
                        if (!be.isPresent()) {
                            bookStatus = new IssueBookStatus(bookId, "Not Issued", "Book Not Found");
                        } else {
                            BookStatusEntity bse = be.get().getBookStatus();
                            if ((bse.getTotalNumberOfCopies() - bse.getNumberOfCopiesIssued()) == 0) {
                                bookStatus = new IssueBookStatus(bookId,"Not Issued", "No copies available");
                            } else {
                                bookStatus = new IssueBookStatus(bookId,"Issued", "Book Issued");
                                issueableBooks.add(be.get());
                            }
                        }
                        issueBookStatusMap.put(bookId, bookStatus);
                    });

            // If there are issue-able books then finally issue them
            if(issueableBooks.size() > 0) {
                ue.setBooks(issueableBooks);
                userRepository.save(ue);
                // Manage the number of issued copies
                issueableBooks.stream()
                        .forEach(be -> {
                            BookStatusEntity bs = be.getBookStatus();
                            bs.setNumberOfCopiesIssued(bs.getNumberOfCopiesIssued() + 1);
                            bookStatusRepository.save(bs);
                        });
            }

            // Set and return final response
            return new IssueBookResponse(issueBookStatusMap);
        } else {
            throw new LibraryResourceNotFoundException(traceId, "Library User Id: " + userId + " Not Found");
        }
    }

    public void returnBooks(int userId, Integer bookId, String traceId) throws LibraryResourceNotFoundException {

        Optional<UserEntity> userEntity = userRepository.findById(userId);

        if(userEntity.isPresent()) {
            UserEntity ue = userEntity.get();
            boolean wasBookIssued = ue.getBooks().stream()
                    .map(bookEntity -> bookEntity.getBookId())
                    .anyMatch(bid -> bid == bookId);

            Optional<BookEntity> be = bookRepository.findById(bookId);
            if (!wasBookIssued) {
                throw new LibraryResourceNotFoundException(traceId, "Book Id: " + bookId + " was not issued to the user");
            } else {
                ue.getBooks().
                BookStatusEntity bse = be.get().getBookStatus();
                if ((bse.getTotalNumberOfCopies() - bse.getNumberOfCopiesIssued()) == 0) {
                    bookStatus = new IssueBookStatus(bookId,"Not Issued", "No copies available");
                } else {
                    bookStatus = new IssueBookStatus(bookId,"Issued", "Book Issued");
                    issueableBooks.add(be.get());
                }
            }
        } else {
            throw new LibraryResourceNotFoundException(traceId, "Library User Id: " + userId + " Not Found");
        }
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
