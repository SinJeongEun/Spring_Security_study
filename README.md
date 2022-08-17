## authority-test
  - BasicAuthenticationFilter(http basic)사용 
  - voter 커스텀하기
  - SpEL 로 객체 값을 가져와서 권한 체크하기
## basic-authentication-test
  - BasicAuthenticationFilter(http basic)사용 
## basic-test
  - UsernamePasswordAuthenticationFilter(form login) 사용
## login-basic
  - UsernamePasswordAuthenticationFilter(form login) 사용
  - roleHierarchy 설정
## login-custom-filter
  - loginFilter 커스텀
  - AuthenticationProvider 커스텀하여 UsernamePasswordAuthenticationToken토큰 인증을 위임하도록 구현
## login-multi-chain
  - config 파일 복수개를 @Order() 로 순서를 명시하여 multichain 구성하기
## login-rememberme
  - UsernamePasswordAuthenticationFilter(form login) 사용
  - PersistentTokenBasedRemembermeServices
## login-session-management
  - UsernamePasswordAuthenticationFilter(form login) 사용
  - PersistentTokenBasedRemembermeServices
  - 세션 인증 정책 설정
## login-userdetails
  - UsernamePasswordAuthenticationFilter(form login) 사용
  - UserDetailsService 커스텀 하기
## paper-app
## security-advanced
  - JWT 토큰 발행
## sns-logins
  - 카카오 로그인 구현
  - 네이버 로그인 구현
  - 소셜 로그인 구현
