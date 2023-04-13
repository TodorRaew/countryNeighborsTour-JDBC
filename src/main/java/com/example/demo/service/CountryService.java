package com.example.demo.service;

import com.example.demo.model.TravelRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface CountryService {

    int calculateCountOfVisits(TravelRequest travelRequest);
    String saveCountry(String name, String abrev, UserDetails userDetails);
    String removeCountry(String name, UserDetails userDetails);
    Map<String, Double> getRates() throws JsonProcessingException;
    StringBuilder convertCurrency(String username, TravelRequest travelRequest) throws JsonProcessingException;
    StringBuilder execute(String username, TravelRequest travelRequest) throws JsonProcessingException;
}
