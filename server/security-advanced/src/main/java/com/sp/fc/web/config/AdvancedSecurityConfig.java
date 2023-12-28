package com.sp.fc.web.config;

import com.sp.fc.user.service.SpUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AdvancedSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SpUserService spUserService;

    @Autowired
    private CorsFilter corsFilter;

    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        JWTLoginFilter loginFilter = new JWTLoginFilter(authenticationManager(), spUserService); // 로그인 처리
        JWTCheckFilter checkFilter = new JWTCheckFilter(authenticationManager(), spUserService);  // 토큰 검증

        http
                .csrf().disable()
                .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 토큰을 사용할 것이므로 세션을 사용하지 않느다.
                )
                .addFilter(corsFilter)  // cors 정책에서 벗어나기 위함이다. / @CrossOfigin은 인증이 필요 없는 경우 사용하고, 인증이 필요한 경우 앞과 같이 설정한다.
                .formLogin().disable()
                .httpBasic().disable()
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class) //UsernamePasswordAuthenticationFilter 자리에 커스텀 필터 위치 시킨다
                .addFilterAt(checkFilter, BasicAuthenticationFilter.class)
                .authorizeRequests()
                .anyRequest().permitAll()
        ;

    }
}
