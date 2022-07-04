package com.sp.fc.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment =  SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BasicAuthenticationTest {

    @LocalServerPort
    int port;

    RestTemplate client = new RestTemplate();

    private String greetingUrl() {
        return "http://localhost:" + port + "/greeting";
    }

    @DisplayName("1. 인증 실패")
    @Test
    void test_1() {
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            String response = client.getForObject(greetingUrl(), String.class);
        });
        assertEquals(401, exception.getRawStatusCode());
    }

    @DisplayName("2. 인증 성공")
    @Test
    void test_2() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString(
                "user1:1111".getBytes(StandardCharsets.UTF_8)
        ));
        HttpEntity entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = client.exchange(greetingUrl(), HttpMethod.GET, entity, String.class);

        assertEquals("hello", response.getBody());
    }

    //TestRespTemplate는 기본적으로 basic token 을 지원한다.
    // basic 토큰을 넣어 request 를 날린다.
    @DisplayName("3. 인증 성공")
    @Test
    void test_3() {
        TestRestTemplate testRestTemplate = new TestRestTemplate("user1", "1111");
        String response = testRestTemplate.getForObject(greetingUrl(), String.class);
        assertEquals("hello", response);
    }

    @DisplayName("4. POST 인증")
    @Test
    void test_4() {
        TestRestTemplate testClient = new TestRestTemplate("user1", "1111");
        ResponseEntity<String> response = testClient.postForEntity(greetingUrl(), "litze", String.class);
        assertEquals("hello litze", response.getBody());
    }
    
}
