package com.skb.course.apis.libraryapis.service;

import com.skb.course.apis.libraryapis.entity.UserEntity;
import com.skb.course.apis.libraryapis.model.User;
import com.skb.course.apis.libraryapis.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User addUser(User userToBeAdded) {
        UserEntity userEntity = new UserEntity(
                userToBeAdded.getFirstName(),
                userToBeAdded.getLastName(),
                userToBeAdded.getDateOfBirth(),
                userToBeAdded.getGender(),
                userToBeAdded.getPhoneNumber(),
                userToBeAdded.getEmailId());

        UserEntity addedUser = userRepository.save(userEntity);
        userToBeAdded.setUserId(addedUser.getUserId());
        return userToBeAdded;
    }
}
