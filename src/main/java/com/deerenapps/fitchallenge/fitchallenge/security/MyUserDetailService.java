package com.deerenapps.fitchallenge.fitchallenge.security;

import com.deerenapps.fitchallenge.fitchallenge.entities.User;
import com.deerenapps.fitchallenge.fitchallenge.model.UserModel;
import com.deerenapps.fitchallenge.fitchallenge.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    @Override
    public MyUserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        List<User> users = userRepository.findByUsername(s);
        if(users.size() == 0){
            throw new UsernameNotFoundException("User details not found for : " + s);
        }

        return new MyUserDetails(users.get(0));
    }

    // UserModel will be data from a user creating or updating an account user
    public User save(UserModel user){

        User userEntity;

        // if existing user only updating password else creating new user
        try {
            userEntity = loadUserByUsername(user.getUsername()).getUser();
        } catch (UsernameNotFoundException e){
            userEntity = new User();
            // get user's requested username
            userEntity.setUsername(user.getUsername());
        }

        // get user's requested password and hash it before saving
        userEntity.setPassword(bcryptEncoder.encode(user.getPassword()));

        // save and return user entity
        return userRepository.save(userEntity);
    }

    public boolean isUsernameUnique(String username) {
        User userByUsername = userRepository.getUserByUsername(username);
        return userByUsername == null;
    }


}
