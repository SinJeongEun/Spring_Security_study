package com.sp.fc.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/greeting")
    public String greeting() {
        return "hello";
    }

    @PostMapping("/check")
    public ResponseEntity<String> checkReq(@RequestBody Map<String, Object> param){
        System.out.println("컨트롤러~");
        System.out.println("request  {}"+param);
        System.out.println("결과 ============");
        System.out.println("내용  " + param.get("id"));
        System.out.println("내용  " + param.get("auth"));

        return ResponseEntity.ok("Success");
    }
}
