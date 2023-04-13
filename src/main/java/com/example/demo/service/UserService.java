package com.example.demo.service;

import com.example.demo.dto.UserDTO;
import com.example.demo.model.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;


public interface UserService {
    String registration(User user);
    //String setUserStatus(User user);
    String setUserStatus(String username);
    Optional<User> findByUsername(String username);
    List<UserDTO> readAllUsers(UserDetails userDetails);
    String deleteById(Integer id, String username);
}
