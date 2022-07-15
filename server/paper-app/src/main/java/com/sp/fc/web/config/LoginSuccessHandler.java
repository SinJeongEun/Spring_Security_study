package com.sp.fc.web.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private RequestCache requestCache = new HttpSessionRequestCache();
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    protected Log logger = LogFactory.getLog(this.getClass());

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException {

        handle(request, response, requestCache.getRequest(request, response), authentication);
        clearAuthenticationAttributes(request);
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authentication
    ) throws IOException, ServletException {
//        System.out.println("success... 1");
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }


    protected void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            SavedRequest savedRequest, //이전 url에 관한 정보
            Authentication authentication
    ) throws IOException {
        String targetUrl = determineTargetUrl(request, savedRequest, authentication); // 기존에 방문하려던 url 정보를 받아두고, 로그인 성공 시에 redirect 한다.
        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to "+ targetUrl);
            return;
        }
        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    //기존에 방문하려했던 url 을 request 에 세팅하는 함수
    protected String determineTargetUrl(final HttpServletRequest request, SavedRequest savedRequest, final Authentication authentication) {
        if(savedRequest != null) {
            String redirectUrl = savedRequest.getRedirectUrl();
            if(redirectUrl != null && !redirectUrl.startsWith("/login")) {
                return savedRequest.getRedirectUrl();
            }
        }
        if(request.getParameter("site").equals("manager")) {
            return "/manager";
        }else if(request.getParameter("site").equals("study")){
            return "/study";
        }else if(request.getParameter("site").equals("teacher")){
            return "/teacher";
        }
        return "/";
    }

}
