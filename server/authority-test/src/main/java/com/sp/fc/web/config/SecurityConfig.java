package com.sp.fc.web.config;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private NameCheck nameCheck;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser(
                        User.withDefaultPasswordEncoder()
                                .username("user1")
                                .password("1111")
                                .roles("USER", "STUDENT")
                ).withUser(
                        User.withDefaultPasswordEncoder()
                                .username("user2")
                                .password("1111")
                                .roles("USER", "STUDENT")
                ).withUser(
                        User.withDefaultPasswordEncoder()
                                .username("tutor1")
                                .password("1111")
                                .roles("USER", "TUTOR")
                )
        ;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .httpBasic().and()
                .authorizeRequests(
                        authority->authority
                                .mvcMatchers("/greeting/{name}")
//                                    .hasRole("USER")
                                .access("@nameCheck.check(#name)") // 이곳에서 SepL 언어 사용 가능하다.
                                .anyRequest().authenticated()
                )
        ;
    }
}
