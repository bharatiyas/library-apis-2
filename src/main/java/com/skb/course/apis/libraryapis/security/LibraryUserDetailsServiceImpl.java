package com.skb.course.apis.libraryapis.security;

import com.skb.course.apis.libraryapis.exception.UserNotFoundException;
import com.skb.course.apis.libraryapis.model.LibraryUser;
import com.skb.course.apis.libraryapis.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// This class will load a given user’s data from the database
// When a user tries to authenticate, following steps are executed:
//      1. This method receives the username
//      2. Searches the database for a record containing the user
//      3. If found, returns an instance of LibraryUser.
//      4. The properties of this instance (username and password) are then checked against the credentials passed by
//          the user in the login request.
//  This last step (#4) is executed outside this class, by the Spring Security framework.
@Service
public class LibraryUserDetailsServiceImpl implements UserDetailsService {

    private UserService userService;

    public LibraryUserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LibraryUser libraryUser =  null;
        try {
            libraryUser = userService.getUserByUserId(username);
        } catch (UserNotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage());
        }

        return libraryUser;
    }
}
