package com.skb.course.apis.libraryapis.service;

import com.skb.course.apis.libraryapis.entity.UserEntity;
import com.skb.course.apis.libraryapis.exception.UserNotFoundException;
import com.skb.course.apis.libraryapis.model.LibraryUser;
import com.skb.course.apis.libraryapis.model.Role;
import com.skb.course.apis.libraryapis.repository.UserRepository;
import com.skb.course.apis.libraryapis.security.SecurityConstants;
import com.skb.course.apis.libraryapis.util.LibraryApiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserRepository userRepository;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public LibraryUser addUser(LibraryUser libraryUserToBeAdded) {
        UserEntity userEntity = new UserEntity(
                // Saving password as plain text isn't a good idea therefore encrypt it
                libraryUserToBeAdded.getUsername(),
                bCryptPasswordEncoder.encode(SecurityConstants.getNewUserDefaultPassword()),
                libraryUserToBeAdded.getFirstName(),
                libraryUserToBeAdded.getLastName(),
                libraryUserToBeAdded.getDateOfBirth(),
                libraryUserToBeAdded.getGender(),
                libraryUserToBeAdded.getPhoneNumber(),
                libraryUserToBeAdded.getEmailId(),
                "USER");

        libraryUserToBeAdded.setPassword(SecurityConstants.getNewUserDefaultPassword());
        UserEntity addedUser = userRepository.save(userEntity);
        libraryUserToBeAdded.setUserId(addedUser.getUserId());
        return libraryUserToBeAdded;
    }

    public LibraryUser getUserByUserId(int userId) throws UserNotFoundException {
        Optional<UserEntity> userEntity = userRepository.findById(userId);
        LibraryUser libraryUser = null;
        if(userEntity.isPresent()) {
            UserEntity ue = userEntity.get();
            libraryUser = createUserFromEntity(ue);
        } else {
            throw new UserNotFoundException("LibraryUser Id: " + userId + " Not Found");
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

    public LibraryUser updateUser(LibraryUser libraryUserToBeUpdated) throws UserNotFoundException {
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
            throw new UserNotFoundException("LibraryUser Id: " + libraryUserToBeUpdated.getUserId() + " Not Found");
        }
        return libraryUser;
    }

    public void deleteUserByUserId(int userId) throws UserNotFoundException {
        userRepository.deleteById(userId);
    }

    public List<LibraryUser> searchUsers(String firstName, String lastName, Integer pageNo, Integer pageSize,
                                         String sortBy) throws UserNotFoundException {
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
            throw new UserNotFoundException("No Users found with First name: " + firstName + " and Last name: " + lastName);
        }
    }

    private LibraryUser createUserFromEntity(UserEntity ue) {
        return new LibraryUser(ue.getUserId(), ue.getUsername(), ue.getFirstName(), ue.getLastName(),
                ue.getDateOfBirth(), ue.getGender(), ue.getPhoneNumber(), ue.getEmailId(), Role.valueOf(ue.getRole()));
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
