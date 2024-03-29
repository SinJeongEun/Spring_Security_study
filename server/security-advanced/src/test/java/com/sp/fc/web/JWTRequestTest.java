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

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    private TokenBox getToken(){
        RestTemplate client = new RestTemplate();
        HttpEntity<UserLoginForm> body = new HttpEntity<>(
                UserLoginForm.builder().username("user1").password("1111").build()
        );
        ResponseEntity<SpUser> resp1 = client.exchange(uri("/login"), HttpMethod.POST, body, SpUser.class);
        return TokenBox.builder().authToken(resp1.getHeaders().get("auth_token").get(0))
                .refreshToken(resp1.getHeaders().get("refresh_token").get(0))
                .build();
    }

    private TokenBox refreshToken(String refreshToken){ // token 갱신 요청 메소드
        RestTemplate client = new RestTemplate();
        HttpEntity<UserLoginForm> body = new HttpEntity<>(
                UserLoginForm.builder().refreshToken(refreshToken).build()
        );
        ResponseEntity<SpUser> resp1 = client.exchange(uri("/login"), HttpMethod.POST, body, SpUser.class);
        return TokenBox.builder().authToken(resp1.getHeaders().get("auth_token").get(0))
                .refreshToken(resp1.getHeaders().get("refresh_token").get(0))
                .build();
    }

    @DisplayName("1. hello 메시지 받아오기")
    @Test
    void test_1() {
        // 인증 받기
        TokenBox token = getToken();

        RestTemplate client = new RestTemplate();
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.AUTHORIZATION, "Bearer "+token.getAuthToken());

        HttpEntity body = new HttpEntity<>(null, header);
        ResponseEntity<String> resp2 = client.exchange(uri("/greeting"), HttpMethod.GET, body, String.class);

        System.out.println( resp2.getBody());
        assertEquals("hello", resp2.getBody());
    }

    @DisplayName("2. 토큰 만료 테스트 ")
    @Test
    void test_2() throws InterruptedException {
        TokenBox token = getToken();

        Thread.sleep(3000);
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.AUTHORIZATION, "Bearer "+token.getAuthToken());

        RestTemplate client = new RestTemplate();
        assertThrows(Exception.class, ()->{
            HttpEntity body = new HttpEntity<>(null, header);
            ResponseEntity<String> resp2 = client.exchange(uri("/greeting"), HttpMethod.GET, body, String.class);
        });

        // 토큰 갱신 요청
        token = refreshToken(token.getRefreshToken());
        HttpHeaders header2 = new HttpHeaders();
        header2.add(HttpHeaders.AUTHORIZATION, "Bearer "+token.getAuthToken());
        HttpEntity body = new HttpEntity<>(null, header2);
        ResponseEntity<String> resp3 = client.exchange(uri("/greeting"), HttpMethod.GET, body, String.class);

        assertEquals("hello", resp3.getBody());
        System.out.println(resp3.getBody());
    }


    @DisplayName("request 조작 및 컨트롤러에서 받기")
    @Test
    void test_modifyRequest() {
        System.out.println(uri("/check"));
        // 인증 받기
        TokenBox token = getToken();

        RestTemplate client = new RestTemplate();
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.AUTHORIZATION, "Bearer "+token.getAuthToken());

        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("form1","111");
        bodyMap.put("form2","222");
        bodyMap.put("form3","333");
        HttpEntity body = new HttpEntity<>(bodyMap, header);
        ResponseEntity<String> resp2 = client.exchange(uri("/check"), HttpMethod.POST, body, String.class);

        System.out.println("res body  {}"+resp2);
    }
}
