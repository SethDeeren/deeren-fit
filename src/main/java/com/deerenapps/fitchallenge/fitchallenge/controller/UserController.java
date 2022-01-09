package com.deerenapps.fitchallenge.fitchallenge.controller;

import com.deerenapps.fitchallenge.fitchallenge.entities.Challenge;
import com.deerenapps.fitchallenge.fitchallenge.entities.User;
import com.deerenapps.fitchallenge.fitchallenge.entities.UserStats;
import com.deerenapps.fitchallenge.fitchallenge.model.UserModel;
import com.deerenapps.fitchallenge.fitchallenge.repos.ChallengeRepository;
import com.deerenapps.fitchallenge.fitchallenge.repos.UserRepository;
import com.deerenapps.fitchallenge.fitchallenge.security.MyUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    MyUserDetailService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ChallengeRepository challengeRepository;

    @GetMapping("/register-form")
    public String getRegisterForm(){
        return "register";
    }


    @PostMapping
    public String saveUser(@ModelAttribute UserModel user){
        userService.save(user);
        return "redirect:/challenges";
    }



}
