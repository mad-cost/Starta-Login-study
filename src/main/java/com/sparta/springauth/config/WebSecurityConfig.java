package com.sparta.springauth.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // Spring Security 지원을 가능하게 함
public class WebSecurityConfig {


  @Bean
  public SecurityFilterChain securityFilterChain(
          HttpSecurity http
  ) throws Exception {
    // CSRF 설정
    http.csrf((csrf) -> csrf.disable());

    http.authorizeHttpRequests((authorizeHttpRequests) ->
            authorizeHttpRequests
                    .requestMatchers(
                            PathRequest.toStaticResources().atCommonLocations())
                    .permitAll() // resources 접근 모두 허용 ex) /css/**, /js/**, /images/**, /favicon.ico..

                    .requestMatchers(
                            "/api/user/**"
                    )
                    .permitAll() // '/api/user/'로 시작하는 요청 모두 접근 허가
                    .anyRequest()
                    .authenticated() // 그 외 모든 요청 인증처리
    );


    /* 로그인 사용 (default 로그인 기능 ID: user)
    http.formLogin(Customizer.withDefaults()); */

    // Session 로그인 사용 방식
    // UserDetailsServiceImpl, UserDetailsImpl을 사용하여 Custom 로그인: default로그인이 아닌 내가 만든 로그인창 사용하기
    http.formLogin((formLogin) ->
            formLogin
                    // 로그인 View 제공 (GET /api/user/login-page)
                    .loginPage("/api/user/login-page")
                    // 로그인 처리 (POST /api/user/login)
                    .loginProcessingUrl("/api/user/login")
                    // 로그인 처리 후 성공 시 URL
                    .defaultSuccessUrl("/")
                    // 로그인 처리 후 실패 시 URL
                    .failureUrl("/api/user/login-page?error")
                    .permitAll()
    );

    return http.build();
  }
}