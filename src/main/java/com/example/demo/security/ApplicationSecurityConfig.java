package com.example.demo.security;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo.security.ApplicationUserRole.ADMIN;
import static com.example.demo.security.ApplicationUserRole.USER;

@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserRepository userRepository;


    public ApplicationSecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/v2/user").permitAll()
                .antMatchers(HttpMethod.DELETE, "/api/v2/userById/{id}").hasRole(ADMIN.name())
                .antMatchers(HttpMethod.GET, "/api/v2/allUsers").hasRole(ADMIN.name())
                .antMatchers("/api/v2/status").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/country/{name}/{abrev}").hasRole(ADMIN.name())
                .antMatchers(HttpMethod.DELETE, "/api/v1/country/{name}").hasRole(ADMIN.name())
                .antMatchers(HttpMethod.GET, "/api/v1/result").authenticated()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic();
    }

    @Override
    @Bean
    protected UserDetailsService userDetailsService() {
        List<User> users = userRepository.readAllUsers();
        List<UserDetails> returnUsers = new ArrayList<>();
        for (User user : users) {
            if (user.getUserName().equals("Todor")){
                returnUsers
                        .add(
                                org.springframework.security.core.userdetails.User
                                        .builder()
                                        .username(user.getUserName())
                                        .password(user.getPassword())
                                        .roles(ADMIN.name())
                                        .build());
            }else {
                returnUsers
                        .add(
                                org.springframework.security.core.userdetails.User
                                        .builder()
                                        .username(user.getUserName())
                                        .password(user.getPassword())
                                        .roles(USER.name())
                                        .build());
            }
        }
        return new InMemoryUserDetailsManager(returnUsers);
    }
}