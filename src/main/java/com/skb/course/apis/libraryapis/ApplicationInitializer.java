package com.skb.course.apis.libraryapis;

import com.skb.course.apis.libraryapis.user.UserEntity;
import com.skb.course.apis.libraryapis.model.common.Gender;
import com.skb.course.apis.libraryapis.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;

// This class is need to initialize the User table with Admin user details. This is needed because for current
// security reasong User API does not support adding a user with Admin role
@Component
public class ApplicationInitializer {

    @Autowired
    Environment env;

    UserRepository userRepository;

    BCryptPasswordEncoder bCryptPasswordEncoder;

    public ApplicationInitializer(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;

    }

    @Value("${library.api.user.admin.username:lib-admin}")
    private String adminUsername;

    @Value("${library.api.user.admin.password:@6m1n!23}")
    private String adminPassword;

    @PostConstruct
    private void init() {

        // insert admin user on application start up, only for the first time
        UserEntity admin = userRepository.findByUsername(adminUsername);
        if(admin == null) {
            UserEntity userEntity = new UserEntity(adminUsername, bCryptPasswordEncoder.encode(adminPassword),
                    "Library", "Admin", LocalDate.now().minusYears(30), Gender.Female, "000-000-000", "library.admin@email.com", "ADMIN");

            userRepository.save(userEntity);
        }
    }


}
