package com.sp.fc.web;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.xml.bind.DatatypeConverter;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

public class JWTSimpleTest {

    private void printToken(String token){
        String[] tokens = token.split("\\.");
        System.out.println("header: "+new String(Base64.getDecoder().decode(tokens[0])));
        System.out.println("body: "+new String(Base64.getDecoder().decode(tokens[1])));
    }

    @DisplayName("1. jjwt 를 이용한 토큰 테스트")
    @Test
    void test_1(){
        String okta_token = Jwts.builder().addClaims(
                Map.of("name", "litze", "price", 3000)
                ).signWith(SignatureAlgorithm.HS256, "litze")
                .compact();
        System.out.println(okta_token);
        printToken(okta_token);

        Jws<Claims> tokenInfo = Jwts.parser().setSigningKey("litze").parseClaimsJws(okta_token);
        System.out.println(tokenInfo);
    }


    @DisplayName("2. java-jwt 를 이용한 토큰 테스트")
    @Test
    void test_2() {

        byte[] SEC_KEY = DatatypeConverter.parseBase64Binary("litze");

        String oauth0_token = JWT.create().withClaim("name", "litze").withClaim("price", 3000)
                .sign(Algorithm.HMAC256(SEC_KEY));

        System.out.println(oauth0_token);
        printToken(oauth0_token);

        // jjwt
        DecodedJWT verified = JWT.require(Algorithm.HMAC256(SEC_KEY)).build().verify(oauth0_token);
        System.out.println(verified.getClaims());

        //// okta
        Jws<Claims> tokenInfo = Jwts.parser().setSigningKey(SEC_KEY).parseClaimsJws(oauth0_token);
        System.out.println(tokenInfo);
        // => 알고리즘과, key 값이 일치한다면, 다른 토큰이라도 검증 가능하다.
    }

    @DisplayName("3. 만료 시간 테스트")
    @Test
    void test_3() throws InterruptedException {
        final Algorithm AL = Algorithm.HMAC256("litze");
        String token = JWT.create().withSubject("a1234")
                .withNotBefore(new Date(System.currentTimeMillis() + 1000)) // 1초 뒤부터 토큰이 유효하다
                .withExpiresAt(new Date(System.currentTimeMillis() + 3000)) // 토큰 만료시간 설정
                .sign(AL);

        try {
            DecodedJWT verify = JWT.require(AL).build().verify(token);
            System.out.println(verify.getClaims());
        }catch(Exception ex){ // 유효하지 않은 토큰이라도 열어볼 수는 있다 ! 
            System.out.println("유효하지 않은 토큰입니다...");
            DecodedJWT decode = JWT.decode(token);
            System.out.println(decode.getClaims());
        }

    }



}
