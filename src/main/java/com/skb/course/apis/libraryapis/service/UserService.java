package com.skb.course.apis.libraryapis.service;

import com.skb.course.apis.libraryapis.entity.UserEntity;
import com.skb.course.apis.libraryapis.exception.UserNotFoundException;
import com.skb.course.apis.libraryapis.model.LibraryUser;
import com.skb.course.apis.libraryapis.model.Role;
import com.skb.course.apis.libraryapis.repository.UserRepository;
import com.skb.course.apis.libraryapis.security.SecurityConstants;
import com.skb.course.apis.libraryapis.util.LibraryApiUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {



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
            libraryUser = createUserFromEntity(userEntity);
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
                ue.setPassword(libraryUserToBeUpdated.getPassword());
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

    private LibraryUser createUserFromEntity(UserEntity ue) {
        return new LibraryUser(ue.getUserId(), ue.getUsername(), ue.getFirstName(), ue.getLastName(),
                ue.getDateOfBirth(), ue.getGender(), ue.getPhoneNumber(), ue.getEmailId(), Role.valueOf(ue.getRole()));
    }
}
