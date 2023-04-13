package com.example.demo.repository;

import java.util.List;
import java.util.Map;

public interface CountryRepository {

    void saveCountry(String name, String abrev);

    int removeCountry(String name);

    Map<String, String> getCurrency();

    List<String> getNeighbors(String startingCountry);

    boolean findCountryByName(String name);
}