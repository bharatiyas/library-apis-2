package com.skb.course.apis.libraryapis;

import com.skb.course.apis.libraryapis.entity.UserEntity;
import com.skb.course.apis.libraryapis.model.Gender;
import com.skb.course.apis.libraryapis.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;

// This class is need to initialize the User table with Admin user details. This is needed because for current
// security reasong User API does not support adding a user with Admin role
@Component
public class ApplicationInitializer {

    UserRepository userRepository;

    BCryptPasswordEncoder bCryptPasswordEncoder;

    public ApplicationInitializer(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostConstruct
    private void init() {
        // insert admin user on application start up
        UserEntity userEntity = new UserEntity("admin", bCryptPasswordEncoder.encode("admin!23"),
                "Library", "Admin", LocalDate.now().minusYears(30), Gender.Female, "000-000000", "library.admin@email.com", "ADMIN");

        userRepository.save(userEntity);
    }
}
