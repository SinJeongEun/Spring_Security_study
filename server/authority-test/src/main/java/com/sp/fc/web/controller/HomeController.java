package com.sp.fc.web.controller;

import com.sp.fc.web.service.SecurityMessageService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@AllArgsConstructor
public class HomeController {

    private final SecurityMessageService securityMessageService;

    @PreAuthorize("@nameCheck.check(#name)")
    @GetMapping("/greeting/{name}")
    public String greeting(@PathVariable String name) {
        return "hello " + securityMessageService.message(name);
    }

    @PostMapping("/check")
    public ResponseEntity<String> checkReq(@RequestBody Map<String, Object> param){
        System.out.println("컨트롤러~");
        System.out.println("request  {}"+param);

        return ResponseEntity.ok("Success");
    }
}
