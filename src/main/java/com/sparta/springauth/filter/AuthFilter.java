package com.sparta.springauth.filter;

import com.sparta.springauth.entity.User;
import com.sparta.springauth.jwt.JwtUtil;
import com.sparta.springauth.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Slf4j(topic = "AuthFilter")
@Component
@Order(2) // Filter 동작 순서 지정 / 두 번째로 동작하는 Filter
// 인증 및 인가에 대한 Filter
public class AuthFilter implements Filter {

  private final UserRepository userRepository;
  private final JwtUtil jwtUtil;

  public AuthFilter(UserRepository userRepository, JwtUtil jwtUtil) {
    this.userRepository = userRepository;
    this.jwtUtil = jwtUtil;
  }

  @Override
  public void doFilter(
          ServletRequest request, // URL 정보를 가져온다
          ServletResponse response,
          FilterChain chain
  ) throws IOException, ServletException {
    HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    String url = httpServletRequest.getRequestURI();


    // StringUtils.hasText(): 공백인지 or Null인지 확인 && startsWith(): "/api/user" or "/css" or "/js"로 시작하는지?
    if (StringUtils.hasText(url) &&
            // 검증을 하지 않는 URL
            (url.startsWith("/api/user") || url.startsWith("/css") || url.startsWith("/js"))
    ) {
      log.info("인증 처리를 하지 않는 URL : " + url);
      // 회원가입, 로그인 관련 API 는 인증 필요없이 요청 진행 -> 회원가입 및 로그인은 인증이 필요가 없다
      chain.doFilter(request, response); // 다음 Filter 로 이동
    } else {
      // 나머지 API 요청은 인증 처리 진행
      // 토큰 확인
      String tokenValue = jwtUtil.getTokenFromRequest(httpServletRequest); //Bearer eyJhbGci....

      // StringUtils.hasText(): 공백인지 or Null인지 확인
      if (StringUtils.hasText(tokenValue)) { // 토큰이 존재하면 검증 시작
        // JWT 토큰 substring -> 순수한 토큰 값: (Bearer 공백)eyJhbGci...
        String token = jwtUtil.substringToken(tokenValue);

        // 토큰 검증 -> 올바른 토큰이면 true 반환
        if (!jwtUtil.validateToken(token)) {
          throw new IllegalArgumentException("Token Error");
        }

        // 토큰에서 사용자 정보 가져오기
        Claims info = jwtUtil.getUserInfoFromToken(token);

        // 사용자의 아이디가 존재하는지
        User user = userRepository.findByUsername(info.getSubject()).orElseThrow(() ->
                new NullPointerException("Not Found User")
        );

        // @Controller에서 req.getAttribute("user");를 통해 인증된 유저 정보 확인 가능
        request.setAttribute("user", user);
        chain.doFilter(request, response); // 다음 Filter 로 이동
      } else {
        throw new IllegalArgumentException("Not Found Token");
      }
    }
  }

}