package com.sp.fc.web.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityMessage { //Authentication 과 어떠한 페이지인지 알려주는 객체

    private Authentication auth;
    private String message;
}
