package com.example.demo.service;

import com.example.demo.exceptions.InvalidInputException;
import com.example.demo.model.TravelRequest;
import com.example.demo.model.User;
import com.example.demo.repository.CountryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CountryServiceImpl implements CountryService {
    private final CountryRepository repository;
    private final UserService userService;

    public CountryServiceImpl(CountryRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    @Override
    public int calculateCountOfVisits(TravelRequest travelRequest) {

        if (travelRequest.getTotalBudget() != null
                && travelRequest.getStartingCountryName() != null
                && travelRequest.getBudgetPerCountry() != null) {

            List<String> countNeighbors = repository.getNeighbors(travelRequest.getStartingCountryName());

            BigDecimal moneyPerOneTour = travelRequest.getBudgetPerCountry().multiply(new BigDecimal(String.valueOf(countNeighbors.size())));

            return travelRequest.getTotalBudget().divide(moneyPerOneTour).intValue();

        }
        throw new InvalidInputException("Invalid input!");
    }

    @Override
    public String saveCountry(String name, String abrev, UserDetails userDetails) {

        Pattern pattern = Pattern.compile("^[a-zA-Z]+(\\s[a-zA-Z]+)*$");
        Matcher matcher = pattern.matcher(name);
        if (!matcher.matches() || name.equalsIgnoreCase("null")){
            throw new InvalidInputException("Incorrect country name");
        }

        pattern = Pattern.compile("^[A-Z]{3}$");
        matcher = pattern.matcher(abrev);

        if (!matcher.matches()){
            throw new InvalidInputException("The currency abrev cannot be null, empty or with length different from 3");
        }

        if (userService.findByUsername(userDetails.getUsername()).isPresent()) {
            if (userService.findByUsername(userDetails.getUsername()).get().getIsOnline() == 1) {
                if (!repository.findCountryByName(name)) {
                    repository.saveCountry(name, abrev);

                    return name + " has registered completely";
                }
                return "Country name already exists!";
            } else {
                throw new InvalidInputException("User is offline");
            }
        }
        throw new InvalidInputException("Wrong username or password");
    }

    @Override
    public String removeCountry(String name, UserDetails userDetails) {

        if (userService.findByUsername(userDetails.getUsername()).isPresent()) {
            if (userService.findByUsername(userDetails.getUsername()).get().getIsOnline() == 1) {
                int rows = repository.removeCountry(name);

                if (rows != 0) {
                    return "Country was deleted successfully";
                }
                return "Country not found!";
            }
            throw new InvalidInputException("User is offline");
        }
        throw new InvalidInputException("Wrong username or password");
    }

    @Override
    public Map<String, Double> getRates() throws JsonProcessingException {

        String API_ACCESS_KEY = "Pnu2lD4cT04LQ3QYKlm0WafDfWQK465B";

        String symbols =
                "BGN, RON, MKD, RSD, " +
                        "TRY, GEL, AMD, IRR, IQD, AZN, " +
                        "SYP, ALL, BAM, HUF, UAH, MDL";
        String base = "EUR";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();

        headers.set("apikey", API_ACCESS_KEY);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("https://api.apilayer.com/exchangerates_data/latest")
                .queryParam("symbols", symbols)
                .queryParam("base", base);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> jsonMap = mapper.readValue(response.getBody(), Map.class);
        Map<String, Double> rates = (Map<String, Double>) jsonMap.get("rates");

        if (!rates.isEmpty()) {
            return rates;
        } else {
            return new TreeMap<>();
        }
    }

    @Override
    public StringBuilder convertCurrency(String username, TravelRequest travelRequest) throws JsonProcessingException {

        Map<String, Double> rates = getRates(); // currency_abrev and rates
        Map<String, String> currencyAbrevs = repository.getCurrency(); // country_name and currency_abrev
        List<String> neighbors = repository.getNeighbors(travelRequest.getStartingCountryName()); // neighbors country name

        Map<String, BigDecimal> convertedMoney = new HashMap<>();

        for (String neighbor : neighbors) {

            if (currencyAbrevs.containsKey(neighbor)) {

                if (!currencyAbrevs.get(neighbor).equalsIgnoreCase("EUR")) {

                    BigDecimal convert = travelRequest.getBudgetPerCountry().multiply(BigDecimal.valueOf((rates.get(currencyAbrevs.get(neighbor)))));
                    convertedMoney.put(currencyAbrevs.get(neighbor), convert);
                } else {
                    convertedMoney.put(currencyAbrevs.get(neighbor), travelRequest.getBudgetPerCountry());
                }
            }
        }

        int countTours = calculateCountOfVisits(travelRequest);
        int countNeighbors = neighbors.size();
        BigDecimal moneyPerOneTour = travelRequest.getBudgetPerCountry().multiply(new BigDecimal(String.valueOf(countNeighbors)));
        int leftMoney = travelRequest.getTotalBudget().subtract(moneyPerOneTour.multiply(BigDecimal.valueOf(countTours))).intValue();

        return outputBuilder(username, travelRequest, currencyAbrevs, neighbors, convertedMoney, countTours, leftMoney);
    }

    private StringBuilder outputBuilder(String username, TravelRequest travelRequest, Map<String, String> currencyAbrevs,
                                        List<String> neighbors, Map<String, BigDecimal> convertedMoney,
                                        int countTours, int leftMoney) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(travelRequest.getStartingCountryName())
                .append(" has ")
                .append(neighbors.size()) // count neighbors
                .append(" neighbor countries ")
                .append(neighbors)
                .append(" and ")
                .append(username)
                .append(" can travel around them ")
                .append(countTours)
                .append(" times. ")
                .append("He will have ")
                .append(leftMoney)
                .append(" EUR leftover. ")
                .append("For ");

        for (int i = 0; i < neighbors.size(); i++) {
            if (convertedMoney.containsKey(currencyAbrevs.get(neighbors.get(i)))) {

                stringBuilder
                        .append(neighbors.get(i))
                        .append(" he will need to buy ")
                        .append(convertedMoney.get(currencyAbrevs.get(neighbors.get(i))))
                        .append(" ")// money
                        .append(currencyAbrevs.get(neighbors.get(i))); // currency
            }

            if (i != neighbors.size() - 1) {
                stringBuilder
                        .append(", for ");
            } else {
                stringBuilder.append(".");
            }
        }
        return stringBuilder;
    }

    @Override
    public StringBuilder execute(String username, TravelRequest travelRequest) throws JsonProcessingException {

        if (!repository.findCountryByName(travelRequest.getStartingCountryName()) ){

            throw new InvalidInputException("There are no country with this name");
        }
        if (travelRequest.getBudgetPerCountry() == null ||
                travelRequest.getBudgetPerCountry().compareTo(BigDecimal.ZERO) < 0){
            throw new InvalidInputException("The budget per country must be at least 0");
        }
        if (travelRequest.getTotalBudget() == null ||
                travelRequest.getTotalBudget().compareTo(BigDecimal.ZERO) < 0){

            throw new InvalidInputException("The total budget must be at least 0");
        }

        if (travelRequest.getBudgetPerCountry().compareTo(travelRequest.getTotalBudget()) > 0){
            throw new InvalidInputException("The budget per country must be less or equal to total budget");
        }

        Optional<User> user = userService.findByUsername(username);

        if (user.isPresent()) {
            if (user.get().getIsOnline() == 1) {
                return convertCurrency(username, travelRequest);
            }
            throw new InvalidInputException(user.get().getUserName() + " is offline");
        }
        throw new InvalidInputException("User " + username + " does not exists");
    }
}