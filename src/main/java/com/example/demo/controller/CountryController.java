package com.example.demo.controller;

import com.example.demo.model.TravelRequest;
import com.example.demo.service.CountryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Validated
@RequestMapping("/api/v1")
@RestController
public class CountryController {
    private final CountryService service;

    public CountryController(CountryService service) {
        this.service = service;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("country/{name}/{abrev}")
    public String saveCountry(@PathVariable String name,
                              @PathVariable String abrev,
                              @AuthenticationPrincipal UserDetails userDetails) {

        return service.saveCountry(name, abrev, userDetails);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("country/{name}")
    public String removeCountry(@PathVariable String name,
                                @AuthenticationPrincipal UserDetails userDetails) {

        return service.removeCountry(name, userDetails);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("result")
    public StringBuilder execute(@RequestParam String startingCountry,
                                 @RequestParam BigDecimal budgetPerCountry,
                                 @RequestParam BigDecimal totalBudget,
                                 @AuthenticationPrincipal UserDetails userDetails) throws JsonProcessingException {
        TravelRequest travelRequest = new TravelRequest(startingCountry, budgetPerCountry, totalBudget);

        return service.execute(userDetails.getUsername(), travelRequest);
    }
}