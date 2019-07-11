package com.skb.course.apis.libraryapis.service;

import com.skb.course.apis.libraryapis.entity.UserEntity;
import com.skb.course.apis.libraryapis.exception.UserNotFoundException;
import com.skb.course.apis.libraryapis.model.User;
import com.skb.course.apis.libraryapis.repository.UserRepository;
import com.skb.course.apis.libraryapis.util.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private static String DEFAULT_PASSWORD = "Password123";
    @Autowired
    private UserRepository userRepository;

    public User addUser(User userToBeAdded) {
        UserEntity userEntity = new UserEntity(
                DEFAULT_PASSWORD,
                userToBeAdded.getFirstName(),
                userToBeAdded.getLastName(),
                userToBeAdded.getDateOfBirth(),
                userToBeAdded.getGender(),
                userToBeAdded.getPhoneNumber(),
                userToBeAdded.getEmailId());

        userToBeAdded.setPassword(DEFAULT_PASSWORD);
        UserEntity addedUser = userRepository.save(userEntity);
        userToBeAdded.setUserId(addedUser.getUserId());
        return userToBeAdded;
    }

    public User getUser(int userId) throws UserNotFoundException {
        Optional<UserEntity> userEntity = userRepository.findById(userId);
        User user = null;
        if(userEntity.isPresent()) {
            UserEntity ue = userEntity.get();
            user = createUserFromEntity(ue);
        } else {
            throw new UserNotFoundException("User Id: " + userId + " Not Found");
        }
        return user;
    }

    public User updateUser(User userToBeUpdated) throws UserNotFoundException {
        Optional<UserEntity> userEntity = userRepository.findById(userToBeUpdated.getUserId());
        User user = null;
        if(userEntity.isPresent()) {
            UserEntity ue = userEntity.get();
            if(Utility.doesStringValueExist(userToBeUpdated.getEmailId())) {
                ue.setEmailId(userToBeUpdated.getEmailId());
            }
            if(Utility.doesStringValueExist(userToBeUpdated.getPhoneNumber())) {
                ue.setPhoneNumber(userToBeUpdated.getPhoneNumber());
            }
            if(Utility.doesStringValueExist(userToBeUpdated.getPassword())) {
                ue.setPassword(userToBeUpdated.getPassword());
            }
            userRepository.save(ue);
            user = createUserFromEntity(ue);
        } else {
            throw new UserNotFoundException("User Id: " + userToBeUpdated.getUserId() + " Not Found");
        }
        return user;
    }

    private User createUserFromEntity(UserEntity ue) {
        return new User(ue.getUserId(), ue.getPassword(), ue.getFirstName(), ue.getLastName(),
                ue.getDateOfBirth(), ue.getGender(), ue.getPhoneNumber(), ue.getEmailId());
    }
}
