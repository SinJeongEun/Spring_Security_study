package com.sp.fc.web.config;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.fc.user.domain.SpUser;
import com.sp.fc.user.service.SpUserService;
import lombok.SneakyThrows;
import ognl.TokenMgrError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTLoginFilter extends UsernamePasswordAuthenticationFilter {

    private ObjectMapper objectMapper = new ObjectMapper();
    private SpUserService spUserService;

    public JWTLoginFilter(AuthenticationManager authenticationManager, SpUserService spUserService) {
        super(authenticationManager);
        this.spUserService = spUserService;
        setFilterProcessesUrl("/login"); // post login 을 처리해준다.
    }
    //사용자 인증처리
    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        UserLoginForm userLoginForm = objectMapper.readValue(request.getInputStream(), UserLoginForm.class);

        if(userLoginForm.getRefreshToken() == null) {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    userLoginForm.getUsername(), userLoginForm.getPassword(), null
            );
            //AuthenticationManager 가 DaoAuthenticationProvider 를 통해서 SpUserService 에서 유저를 가지고 검증 한다.
            return getAuthenticationManager().authenticate(token);

        }else {
            VerifyResult verifyResult = JWTUtil.verify(userLoginForm.getRefreshToken());
            if(verifyResult.isSuccess()){
                //refresh 가 있는 경우에는 AuthenticationManager에게 위임하지 않고 직접 토큰을 갱신한다.
                SpUser user = (SpUser) spUserService.loadUserByUsername(verifyResult.getUsername());
                return new UsernamePasswordAuthenticationToken(
                        user, user.getAuthorities()
                );
            }else {
                throw new TokenExpiredException("refresh token expired");
            }
        }

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        SpUser user = (SpUser) authResult.getPrincipal();
        //JWT 토큰 발행하기
        response.setHeader("auth_token", JWTUtil.makeAuthToken(user));
        response.setHeader("refresh_token", JWTUtil.makeRefreshToken(user));
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.getOutputStream().write(objectMapper.writeValueAsBytes(user));
    }
}
