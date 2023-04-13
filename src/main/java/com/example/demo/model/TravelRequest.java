package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TravelRequest {

    private String startingCountryName;
    private BigDecimal budgetPerCountry;
    private BigDecimal totalBudget;
}
