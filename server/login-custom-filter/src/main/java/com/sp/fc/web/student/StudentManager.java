package com.sp.fc.web.student;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Set;

@Component
public class StudentManager implements AuthenticationProvider, InitializingBean { // provider 역할이다. 통행증을 발급해준다. student의 명단을 가지고 있다.

    private HashMap<String, Student> studentDB = new HashMap<>(); // 원래는 실제 db 와 통신해야 되지만 우선 이렇게 작업하겠다.

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        StudentAuthenticationToken token = (StudentAuthenticationToken) authentication;
        if(studentDB.containsKey(token.getCredentials())){
            Student student = studentDB.get(token.getCredentials());
            return  StudentAuthenticationToken.builder()
                    .principal(student)
                    .details(student.getUsername())
                    .authenticated(true)
                    .build();
        }
        return null; // db 에 없는 경우는 authenticated = false 가 아닌 null 을 꼭 반환해야 한다. 자신의 담당이 아닌데 핸들링 한 것이 되므로 문제가 된다.
    }

    @Override
    public boolean supports(Class<?> authentication) { // 인증을 위임 받겠다는 의미
        return authentication == StudentAuthenticationToken.class ;  //우리는 CustomLoginFilter 를 통해 토큰을 받을 거기 때문에
        //StudentAuthenticationToken 클래스 형태의 토큰이 들어오면 이의 provider로 동작하겠다는 의미이다.
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // bean 이 초기화 되면 값을 다시 채워넣는다.
        Set.of(
                new Student("hong", "홍길동", Set.of(new SimpleGrantedAuthority("ROLE_STUDENT"))),
                new Student("kang", "강아지", Set.of(new SimpleGrantedAuthority("ROLE_STUDENT"))),
                new Student("rang", "호랑이", Set.of(new SimpleGrantedAuthority("ROLE_STUDENT")))
        ).forEach(s->
                studentDB.put(s.getId(), s));
    }
}