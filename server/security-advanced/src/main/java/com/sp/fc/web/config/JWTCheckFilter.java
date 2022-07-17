package com.sp.fc.web.config;

import com.sp.fc.user.service.SpUserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTCheckFilter extends BasicAuthenticationFilter { //request 가 올 때 마다 토큰 검사 후, securityContextHolder에 유저 정보를 채워주는 역할을 한다.

    private SpUserService spUserService;

    public JWTCheckFilter(AuthenticationManager authenticationManager, SpUserService spUserService) {
        super(authenticationManager);
        this.spUserService = spUserService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        super.doFilterInternal(request, response, chain);
    }
}
