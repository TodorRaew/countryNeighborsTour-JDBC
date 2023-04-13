package com.example.demo.repository;

import com.example.demo.model.Country;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CountryRepositoryImpl implements CountryRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public CountryRepositoryImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public void saveCountry(String name, String abrev) {

        String sql = "insert into country (country_name, currency_abrev) values (:country_name, :currency_abrev)";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("country_name", name)
                .addValue("currency_abrev", abrev);

        namedParameterJdbcTemplate.update(sql, parameterSource);
    }

    @Override
    public int removeCountry(String name) {
        String sql = "DELETE "
                + "   FROM country "
                + "   WHERE country_name = :name";

        return namedParameterJdbcTemplate.update(sql,
                new MapSqlParameterSource("name", name));
    }

    @Override
    public Map<String, String> getCurrency() {
        String query = "SELECT country_name, currency_abrev FROM country";

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        List<Map<String, Object>> rows = namedParameterJdbcTemplate.queryForList(query, parameterSource);
        Map<String, String> data = new HashMap<>();

        for (Map<String, Object> row : rows) {
            String key = row.get("country_name").toString();
            String value = row.get("currency_abrev").toString();
            data.put(key, value);
        }
        return data;
    }

    @Override
    public List<String> getNeighbors(String startingCountry) {
        String sql =
                "       select neighbor_country_name as neighbors   " +
                        "       from neighbor                          " +
                        "       where starting_country_name = :name         ";

        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("name", startingCountry);


        return namedParameterJdbcTemplate.queryForList(sql, parameterSource, String.class);
    }

    @Override
    public boolean findCountryByName(String name) {

        String sql = "SELECT * "
                + "   FROM country"
                + "   WHERE country_name = :name";

        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("name", name);

        try {
            namedParameterJdbcTemplate.queryForObject(sql, parameterSource, (rs, rowNum) ->
                    new Country(
                            rs.getString("country_name"),
                            rs.getString("currency_abrev")
                    ));
        }catch (EmptyResultDataAccessException e){
            return false;
        }
        return true;
    }
}