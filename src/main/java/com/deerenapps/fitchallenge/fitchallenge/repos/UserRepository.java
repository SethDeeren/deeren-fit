package com.deerenapps.fitchallenge.fitchallenge.repos;

import com.deerenapps.fitchallenge.fitchallenge.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {
    List<User> findByUsername(String username);

    User getUserByUsername(String username);
}
