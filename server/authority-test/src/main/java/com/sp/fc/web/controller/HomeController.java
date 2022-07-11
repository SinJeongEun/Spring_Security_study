package com.sp.fc.web.controller;

import com.sp.fc.web.service.SecurityMessageService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class HomeController {

    private final SecurityMessageService securityMessageService;

    @PreAuthorize("@nameCheck.check(#name)")
    @GetMapping("/greeting/{name}")
    public String greeting(@PathVariable String name) {
        return "hello " + securityMessageService.message(name);
    }
}
