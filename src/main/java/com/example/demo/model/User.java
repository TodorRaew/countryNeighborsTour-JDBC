package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {

    private int userId;

    @NotBlank(message = "The username cannot be blank")
    @Pattern(regexp = "^[a-zA-Z0-9_-]{2,50}$",  message = "Incorrect username")
    private String userName;

    @NotBlank(message = "The password cannot be blank or null")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{5,20}$", message = "Incorrect password")
    private String password;

    @Min(0)
    @Max(1)
    private Integer isOnline = 0;
}