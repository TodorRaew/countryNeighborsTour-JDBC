package com.example.demo.service;

import com.example.demo.dto.UserDTO;
import com.example.demo.exceptions.InvalidInputException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public String registration(User user) {
        if (user.getUserName() == null || user.getUserName().isEmpty()) {
            throw new InvalidInputException("User name cannot be null or empty");
        } else if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new InvalidInputException("User password cannot be null or empty");
        }

        UserDTO dto = new UserDTO(user.getUserName());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if (!findByUsername(user.getUserName()).isPresent()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.registration(user);

            return dto.getUserName() + " has registered completely";
        }
        return "Username already exists!";
    }

    @Override
    public String setUserStatus(String username) {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isPresent()) {

            if (user.get().getIsOnline() == 1) {
                userRepository.setUserStatus(user.get().getUserName());
                return username + " has logged out successfully";
            } else {
                userRepository.setUserStatus(user.get().getUserName());
                return username + " has logged in successfully";
            }
        }
        return "Invalid username!";
    }

    @Override
    public Optional<User> findByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isPresent()) {

            if (user.get().getUserName().equalsIgnoreCase(username)) {

                return user;
            }
        }
        return Optional.empty();
    }

    @Override
    public List<UserDTO> readAllUsers(UserDetails userDetails) {
        if (findByUsername(userDetails.getUsername()).isPresent()) {
            if (findByUsername(userDetails.getUsername()).get().getIsOnline() == 1) {
                List<User> users = userRepository.readAllUsers();
                List<UserDTO> dtoList = new ArrayList<>();

                for (User user : users) {

                    dtoList.add(new UserDTO(user.getUserName()));
                }
                return dtoList;
            }
            throw new InvalidInputException(userDetails.getUsername() + " is offline");
        }
        throw new InvalidInputException("User " + userDetails.getUsername() + " does not exists");
    }

    @Override
    public String deleteById(Integer id, String username) {
        Optional<User> user = findByUsername(username);
        if (!user.isPresent()){
            throw new InvalidInputException("User " + username + " does not exists");
        }
        else {
            if (user.get().getIsOnline() == 0){
                throw new InvalidInputException(username + " is offline");
            }
        }

        if (id == null || id <= 0) {
            throw new InvalidInputException("The input cannot be null, less than zero or equal to zero");
        }

        int rows = userRepository.deleteById(id);
        if (rows != 0) {
            return "User deleted successfully";
        }
        return "User not found!";
    }
}