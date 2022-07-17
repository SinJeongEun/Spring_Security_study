package com.sp.fc.web.config;

import com.sp.fc.user.domain.SpUser;
import com.sp.fc.user.service.SpUserService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.security.sasl.AuthenticationException;
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

        String bearer = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(bearer == null || !bearer.startsWith("Bearer ")){ // bearer 토큰이 아닐 경우 다른 필터로 흘려 보낸다.
            chain.doFilter(request, response);
        }
        String token = bearer.substring("Bearer ".length());
        VerifyResult verifyResult = JWTUtil.verify(token);

        if(verifyResult.isSuccess()) {
            SpUser user = (SpUser) spUserService.loadUserByUsername(verifyResult.getUsername());
            UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(
                    user.getUsername(), null, user.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(userToken); // securityContext 에 인증된 토큰을 넣어준다.
        } else {
            throw new AuthenticationException("token is not valid");
        }
        chain.doFilter(request, response);
    }


}
