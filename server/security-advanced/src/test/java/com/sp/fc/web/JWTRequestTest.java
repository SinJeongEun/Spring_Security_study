package com.sp.fc.web;

import com.sp.fc.user.domain.SpUser;
import com.sp.fc.user.repository.SpUserRepository;
import com.sp.fc.user.service.SpUserService;
import com.sp.fc.web.config.UserLoginForm;
import com.sp.fc.web.test.MyWebIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JWTRequestTest extends MyWebIntegrationTest {

    @Autowired
    private SpUserRepository spUserRepository;

    @Autowired
    private SpUserService spUserService;

    @BeforeEach
    void before() {
        spUserRepository.deleteAll();

        SpUser spUser = spUserService.save(SpUser.builder()
                .email("user1")
                .password("1111")
                .enabled(true)
                .build());
        spUserService.addAuthority(spUser.getUserId(), "ROLE_USER");
    }

    @DisplayName("1. hello 메시지 받아오기")
    @Test
    void test_1() {
        // 인증 받기
        RestTemplate client = new RestTemplate();
        HttpEntity<UserLoginForm> body = new HttpEntity<>(
                UserLoginForm.builder().username("user1").password("1111").build()
        );

        ResponseEntity<SpUser> res1 = client.exchange(uri("/login"), HttpMethod.POST, body, SpUser.class);
        System.out.println(res1.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0));
        System.out.println(res1.getBody());

        // greeting 받기 이때는 JWTCheckFilter 로 인증된 유저인지 검사가 필요하다.
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.AUTHORIZATION, res1.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0));
        body = new HttpEntity<>(null, header);
        ResponseEntity<String> resp2 = client.exchange(uri("/greeting"), HttpMethod.GET, body, String.class);

        assertEquals("hello", resp2.getBody());

    }
}
