package com.sp.fc.web.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class HomeController {

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/")
    public Object greeting(@AuthenticationPrincipal Object user){
        return user;
    }

    @GetMapping("/login")
    public ModelAndView login() {
        ModelAndView mvc = new ModelAndView("loginForm");
        return mvc;
    }

    @GetMapping("/index")
    public ModelAndView index(Authentication authentication) {
        System.out.println(authentication);
        ModelAndView mvc = new ModelAndView("index");
        return mvc;
    }

}
