package com.deerenapps.fitchallenge.fitchallenge.restcontroller;

import com.deerenapps.fitchallenge.fitchallenge.entities.User;
import com.deerenapps.fitchallenge.fitchallenge.model.UserModel;
import com.deerenapps.fitchallenge.fitchallenge.repos.UserRepository;
import com.deerenapps.fitchallenge.fitchallenge.security.MyUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    @Autowired
    UserRepository repo;

    @Autowired
    MyUserDetailService userService;

    @GetMapping("/")
    public List<User> getUsers(){
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable long id){
        try{
            return repo.getById(id);
        } catch (Exception e) {
            return null;
        }
    }

    @PostMapping("/")
    public User saveUser(@RequestBody UserModel user){
       return userService.save(user);

    }

    @PostMapping("/check_username")
    public String checkDuplicateUsername(@Param("username") String username) {
        return userService.isUsernameUnique(username) ? "OK" : "Duplicated";
    }


}
