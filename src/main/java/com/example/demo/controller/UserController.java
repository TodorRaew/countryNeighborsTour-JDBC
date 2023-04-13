package com.example.demo.controller;

import com.example.demo.dto.UserDTO;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/api/v2")
public class UserController {
    private final UserService userService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("user")
    public String registration(@Valid @RequestBody User user) {
        return userService.registration(user);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("allUsers")
    public List<UserDTO> getAllUsers(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.readAllUsers(userDetails);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("userById/{id}")
    public String deleteById(@PathVariable("id") Integer id,
                             @AuthenticationPrincipal UserDetails userDetails) {
        return userService.deleteById(id, userDetails.getUsername());
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("status")
    public String setUserStatus(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.setUserStatus(userDetails.getUsername());
    }
}