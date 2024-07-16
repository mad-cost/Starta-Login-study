package com.sparta.springauth.config;

import com.sparta.springauth.jwt.JwtAuthorizationFilter;
import com.sparta.springauth.jwt.JwtAuthenticationFilter;
import com.sparta.springauth.jwt.JwtUtil;
import com.sparta.springauth.security.UserDetailsServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // Spring Security 지원을 가능하게 함
@EnableGlobalMethodSecurity(securedEnabled = true) //
public class WebSecurityConfig {

  // jwtUtil, userDetailsService 아래서 빈 등록 (JwtAuthorizationFilter()에서 사용)
  private final JwtUtil jwtUtil;
  private final UserDetailsServiceImpl userDetailsService;

  // 아래서 빈 등록 AuthenticationConfiguration를 통해서 AuthenticationManager를 사용할 수 있다
  private final AuthenticationConfiguration authenticationConfiguration;


  // @AllArgsConstructor
  public WebSecurityConfig(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService, AuthenticationConfiguration authenticationConfiguration) {
    this.jwtUtil = jwtUtil;
    this.userDetailsService = userDetailsService;
    this.authenticationConfiguration = authenticationConfiguration;
  }

  @Bean
  // AuthenticationConfiguration 빈 등록: AuthenticationManager를 사용하기 위해
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
    // AuthenticationConfiguration을 통하여 AuthenticationManage를 가져온다
    return configuration.getAuthenticationManager();
  }

  @Bean // 인증 필터 빈으로 등록
  public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
    // JwtAuthenticationFilter 인증 필터 객체 생성
    JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil);
    // JwtAuthenticationFilter클래스에는 getAuthenticationManager()가 있다
    // authenticationManager를 사용할 수 있게 세팅하기
    filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
    return filter;
  }

  @Bean
  // JwtAuthorizationFilter클래스의 JwtAuthorizationFilter()
  public JwtAuthorizationFilter jwtAuthorizationFilter() {
    return new JwtAuthorizationFilter(jwtUtil, userDetailsService);
  }

  @Bean
  public SecurityFilterChain securityFilterChain(
          HttpSecurity http
  ) throws Exception {
    // CSRF 설정
    http.csrf(AbstractHttpConfigurer::disable);

    // Session 방식이 아닌 JWT 방식을 사용하기 위한 설정: STATELESS
    http.sessionManagement((sessionManagement) ->
            sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    );

    http.authorizeHttpRequests((authorizeHttpRequests) ->
            authorizeHttpRequests
                    .requestMatchers(
                            PathRequest.toStaticResources().atCommonLocations()
                    )
                    .permitAll() // resources 접근 허용 설정
                    .requestMatchers("/api/user/**").permitAll() // '/api/user/'로 시작하는 요청 모두 접근 허가
                    .anyRequest().authenticated() // 그 외 모든 요청 인증처리
    );

    http.formLogin((formLogin) ->
            formLogin
                    .loginPage("/api/user/login-page")
                    .permitAll()
    );

    // @Secured에 대한 접근 불가 페이지 설정
    http.exceptionHandling((exceptionHandling) ->
            exceptionHandling
                    .accessDeniedPage( //Denied 당했을 때, 이동할 페이지
                            "/forbidden.html"
                    )
    );

    // 필터 관리 / 실행 순서: JwtAuthorizationFilter -> JwtAuthenticationFilter -> UsernamePasswordAuthenticationFilter
    // 인증보다 인가를 먼저 하는 이유: 인가를 먼저 하고 인가가 제대로 되지 않으면 로그인을 진행한다 / 즉, 토큰 검증이 완료가 되고 인증 처리가 되어야 한다
    // jwtAuthenticationFilter가 실행기 전에 jwtAuthorizationFilter를 먼저 실행한다
    http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);
    // UsernamePasswordAuthenticationFilter가 실행되기 전에 jwtAuthenticationFilter를 먼저 실행한다
    http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}