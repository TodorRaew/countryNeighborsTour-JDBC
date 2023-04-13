package com.example.demo.repository;

import com.example.demo.model.User;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Override
    public void registration(User user) {
        String sql = "INSERT INTO user (user_name, password, is_online) "
                + "   VALUES (:username, :password,:isOnline)";

        namedParameterJdbcOperations.update(sql,
                new MapSqlParameterSource("username", user.getUserName())
                        .addValue("password", user.getPassword())
                        .addValue("isOnline", 0));
    }

    @Override
    public int deleteById(Integer id) {
        String sql = "DELETE "
                + "   FROM user "
                + "   WHERE user_id = :user_id";

        return namedParameterJdbcOperations.update(sql,
                new MapSqlParameterSource("user_id", id));
    }

    @Override
    public List<User> readAllUsers() {
        String sql = "SELECT *"
                + "       FROM user ";

        return namedParameterJdbcOperations.query(sql, new MapSqlParameterSource(),
                UserRepositoryImpl::mapRow);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * "
                + "   FROM user"
                + "   WHERE user_name = :username";

        try {

            return Optional.ofNullable(namedParameterJdbcOperations.queryForObject(sql,
                    new MapSqlParameterSource("username", username),
                    UserRepositoryImpl::mapRow));
        } catch (EmptyResultDataAccessException e) {

            return Optional.empty();
        }    }

    @Override
    public void setUserStatus(String username) {
        String sql;
        if (findByUsername(username).isPresent()) {
            if (findByUsername(username).get().getIsOnline() == 1) {

                sql = "UPDATE user "
                        + " SET is_online = 0 "
                        + " WHERE user_name = :username";
            } else {

                sql = "UPDATE user "
                        + " SET is_online = 1 "
                        + " WHERE user_name = :username";
            }
            namedParameterJdbcOperations.update(sql, new MapSqlParameterSource("username", username));
        }
    }

    private static User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User currentUser = new User();

        currentUser.setUserId(rs.getInt("user_id"));
        currentUser.setUserName(rs.getString("user_name"));
        currentUser.setPassword(rs.getString("password"));
        currentUser.setIsOnline(rs.getInt("is_online"));

        return currentUser;
    }
}