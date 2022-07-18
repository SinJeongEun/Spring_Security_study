package com.sp.fc.web.config;

import com.sp.fc.user.domain.SpOAuth2User;
import com.sp.fc.user.domain.SpUser;
import com.sp.fc.user.service.SpUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class SpOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private SpUserService spUserService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        Object principal = authentication.getPrincipal();
        if(principal instanceof OidcUser) {
            // google
            // oidcUser 를 SpOAuth2User 변환한다.
            // loadUser() 로 SpUser 로 매핑한다.
            // SecurityContextHolder 에 인증된 사용자로 넣어준다.
            SpOAuth2User oauth = SpOAuth2User.OAuth2Provider.google.convert((OidcUser) principal);
            SpUser user = spUserService.load(oauth);
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
            );

        }else if(principal instanceof OAuth2User) {
            //naver
            SpOAuth2User oauth = SpOAuth2User.OAuth2Provider.naver.convert((OAuth2User) principal);
            SpUser user = spUserService.load(oauth);
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
            );
        }

        //success redirect url 를 지정해야 됨
        request.getRequestDispatcher("/").forward(request, response);
    }
}
