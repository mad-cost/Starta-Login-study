package com.sparta.springauth.jwt;

import com.sparta.springauth.security.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "JWT 검증 및 인가")
// Jwt 검증 및 인가 처리 필터
public class JwtAuthorizationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil; // Jwt 검증하기 위해 사용
  private final UserDetailsServiceImpl userDetailsService;
  // userDetailsService.loadUserByUsername(username): 해당 유저가 존재하는지 찾기 위해 사용

  public JwtAuthorizationFilter(
          JwtUtil jwtUtil,
          UserDetailsServiceImpl userDetailsService) {
    this.jwtUtil = jwtUtil;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(
          HttpServletRequest req,
          HttpServletResponse res,
          FilterChain filterChain
  ) throws ServletException, IOException {

    // HttpServletRequest 에서 Cookie의 Value: JWT 가져오기
    // Value(Bearer%20eyJhbGciOiJIUzI...)값을 디코딩 하여 가져온다 -> Bearer eyJhbGciOiJIUzI...
    String tokenValue = jwtUtil.getTokenFromRequest(req); // Bearer eyJhbGciOiJIUzI...

    // StringUtils.hasText(): 공백인지 or Null인지 확인
    if (StringUtils.hasText(tokenValue)) {
      // JWT 토큰 substring: 순수 토큰 값 가져오기 -> eyJhbGciOiJIUzI...
      tokenValue = jwtUtil.substringToken(tokenValue);
      log.info(tokenValue);

      // 토큰 검증: 문제가 없을경우 true 반환
      if (!jwtUtil.validateToken(tokenValue)) {
        log.error("Token Error");
        return;
      }

      // 검증된 토큰의 사용자 정보 가져오기 / 사용자 정보는 Claims에 담겨있다
      Claims info = jwtUtil.getUserInfoFromToken(tokenValue);
      // info: ex) {sub=Jun, auth=USER, exp=1721053985, iat=1721050385}

      try {
        // SecurityContextHolder/SecurityContext/Authentication에 사용자 정보 넣어주기
        log.info("인가 select가 실행되는 부분");
        setAuthentication(info.getSubject());
      } catch (Exception e) {
        log.error(e.getMessage());
        return;
      }
    }

    filterChain.doFilter(req, res); // 다음 Filter 로 이동
  }


  // 인증 처리 / SecurityContextHolder/SecurityContext/Authentication에 사용자 정보 넣어주기
  public void setAuthentication(String username) {
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    Authentication authentication = createAuthentication(username);
    context.setAuthentication(authentication);

    SecurityContextHolder.setContext(context);
  }

  // 인증 객체 생성
  private Authentication createAuthentication(String username) {
    UserDetails userDetails = userDetailsService.loadUserByUsername(username); // 해당 유저가 존재하는지
    return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
  }
}