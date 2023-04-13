package com.example.demo.repository;

import com.example.demo.model.User;

import java.util.List;
import java.util.Optional;


public interface UserRepository {

    void registration(User user);

    int deleteById(Integer id);

    List<User> readAllUsers();

    Optional<User> findByUsername(String username);

    void setUserStatus(String username);
}