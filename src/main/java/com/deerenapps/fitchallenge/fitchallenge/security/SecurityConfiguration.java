package com.deerenapps.fitchallenge.fitchallenge.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/api/users/check_username").permitAll()
                .antMatchers("/challenges").permitAll()
                .antMatchers("/challenges/*").permitAll()
                .antMatchers("/challenges/*/charts").permitAll()
                .antMatchers("/users").permitAll()
                .antMatchers("/users/*").permitAll()
                .antMatchers("/my_stats").authenticated()
                .antMatchers("/api/my_stats").authenticated()
                // to authenticate any request not mentioned without this all not mentioned wouuld be permitted
                .anyRequest()
                .authenticated()
                .and().formLogin().defaultSuccessUrl("/challenges")
                .and().httpBasic()
                .and()
                .logout()
                .logoutSuccessUrl("/challenges")
                ;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
