package com.deerenapps.fitchallenge.fitchallenge.controller;

import com.deerenapps.fitchallenge.fitchallenge.entities.Users;
import com.deerenapps.fitchallenge.fitchallenge.model.UserModel;
import com.deerenapps.fitchallenge.fitchallenge.repos.ChallengeRepository;
import com.deerenapps.fitchallenge.fitchallenge.repos.UserRepository;
import com.deerenapps.fitchallenge.fitchallenge.security.MyUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @GetMapping("/edit-form")
    public String getUserEditForm(Model model){
        // Get the user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Users user = userRepository.findByUsername(username).get(0);

        model.addAttribute("user",user);
        return "user-edit-form";
    }


    @PostMapping
    public String saveUser(@ModelAttribute UserModel user){
        userService.save(user);
        return "redirect:/challenges";
    }



}
