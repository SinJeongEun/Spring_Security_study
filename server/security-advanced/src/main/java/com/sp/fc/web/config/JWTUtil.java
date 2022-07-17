package com.sp.fc.web.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sp.fc.user.domain.SpUser;

import java.time.Instant;

public class JWTUtil {

    private static final Algorithm ALGORITHM = Algorithm.HMAC256("litze");
    private static final long AUTH_TIME = 20 * 60;
    private static final long REFRESH_TIME = 60 * 60 * 24 * 7;

    public static String makeAuthToken(SpUser user) { //인증 토큰 만들기
        return JWT.create()
                .withSubject(user.getUsername()) // 최소한의 정보만 토큰에 담았다.
                .withClaim("exp", Instant.now().getEpochSecond() + AUTH_TIME) // 토큰의 유효기간
                .sign(ALGORITHM); // 서명
    }

    public static String makeRefreshToken(SpUser user) { // refresh 토큰 만들기
        return JWT.create()
                .withSubject(user.getUsername())
                .withClaim("exp", Instant.now().getEpochSecond() + REFRESH_TIME)
                .sign(ALGORITHM);
    }

    //토큰이 유효한지 검사하는 메소드
    public static VerifyResult verify(String token) {
        try {
            DecodedJWT verify = JWT.require(ALGORITHM).build().verify(token);
            return VerifyResult.builder()
                    .success(true)
                    .username(verify.getSubject()).build();
        }catch (Exception ex) {
            //토큰이 만료되어도 정보를 열람할 수 있도록 코딩함
            DecodedJWT decodedJWT = JWT.decode(token);
            return VerifyResult.builder()
                    .success(false)
                    .username(decodedJWT.getSubject()).build();
        }
    }
}
