package com.sp.fc.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.fc.user.domain.SpUser;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTLoginFilter extends UsernamePasswordAuthenticationFilter {

    private ObjectMapper objectMapper = new ObjectMapper();

    public JWTLoginFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
        setFilterProcessesUrl("/login"); // post login 을 처리해준다.
    }

    //사용자 인증처리
    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        UserLoginForm userLoginForm = objectMapper.readValue(request.getInputStream(), UserLoginForm.class);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                userLoginForm.getUsername(), userLoginForm.getPassword(), null
        );

        return getAuthenticationManager().authenticate(token);
        //AuthenticationManager 가 DaoAuthenticationProvider 를 통해서 SpUserService 에서 유저를 가지고 검증 한다.
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        SpUser user = (SpUser) authResult.getPrincipal();

        //JWT 토큰 발행하기
        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + JWTUtil.makeAuthToken(user));
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.getOutputStream().write(objectMapper.writeValueAsBytes(user));
    }
}
