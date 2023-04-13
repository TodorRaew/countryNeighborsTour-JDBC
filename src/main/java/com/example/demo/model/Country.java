package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Country {

    @NotEmpty(message = "The country name cannot be null or empty")
    @NotBlank(message = "The country name cannot be blank")
    @Length(min = 1, max = 50, message = "The country name length should be between {min} and {max} characters")
    private String countryName;

    @NotEmpty(message = "The currency abbreviation cannot be null or empty")
    @NotBlank(message = "The currency abbreviation cannot be blank")
    @Length(min = 3, max = 3, message = "The currency abbreviation length must be {max} characters")
    private String currencyAbrev;
}