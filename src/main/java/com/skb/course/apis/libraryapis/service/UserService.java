package com.skb.course.apis.libraryapis.service;

import com.skb.course.apis.libraryapis.entity.UserEntity;
import com.skb.course.apis.libraryapis.model.User;
import com.skb.course.apis.libraryapis.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public User getUser(int userId) {
        Optional<UserEntity> userEntity = userRepository.findById(userId);
        User user = null;
        if(userEntity.isPresent()) {
            UserEntity ue = userEntity.get();
            user = new User(ue.getUserId(), ue.getPassword(), ue.getFirstName(), ue.getLastName(),
                    ue.getDateOfBirth(), ue.getGender(), ue.getPhoneNumber(), ue.getEmailId());
        }

        return user;
    }
}
