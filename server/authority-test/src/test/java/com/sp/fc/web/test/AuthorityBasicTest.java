package com.sp.fc.web.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthorityBasicTest {

    TestRestTemplate client;

    @LocalServerPort
    int port;

    public URI uri(String path) {
        try {
            return new URI(format("http://localhost:%d%s", port, path));
        }catch(Exception ex){
            throw new IllegalArgumentException();
        }
    }

    @DisplayName("1. greeting 메시지를 불러온다.")
    @Test
    void test_1() {
        client = new TestRestTemplate("user1","1111");
        ResponseEntity<String> response = client.getForEntity(uri("/greeting/litze"), String.class);

        assertEquals("hello litze", response.getBody());
        System.out.println(response.getBody());
    }
}
