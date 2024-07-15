package com.sparta.springauth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.springauth.dto.LoginRequestDto;
import com.sparta.springauth.entity.UserRoleEnum;
import com.sparta.springauth.security.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j(topic = "로그인 및 JWT 생성")
// Jwt 인증 처리 필터
/* UsernamePasswordAuthenticationFilter(SecurityFilterChain): 사용자가 username, password를 보내면,
    인증 객체인 UsernamePasswordAuthenticationToken을 만들고, AuthenticationManger를 통해서 확인을 하고,
    성공하면 AuthenticationManger가 SecurityContextHolder에 담아준다
    이 과정을 직접 하는 이유는 인증을 성공하면 Jwt까지 생성을 해줘야 하기 때문이다
    즉, UsernamePasswordAuthenticationFilter를 그대로 사용하면 Jwt방식이 아니라 Session방식이기 때문에 우리가 직접 Custom해서 만들어주기 위해서 */
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
  private final JwtUtil jwtUtil;

  // private final JwtUtil jwtUtil;의 생성자 주입
  public JwtAuthenticationFilter(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
    // setFilterProcessesUrl(): Controller까지 가지 않고, 필터에서 로그인 처리하는 방식([Post] /api/user/login)
    setFilterProcessesUrl("/api/user/login");
  }

  @Override
  // attemptAuthentication(): 로그인 시도하는 메서드
  public Authentication attemptAuthentication(
          HttpServletRequest request,
          HttpServletResponse response
  ) throws AuthenticationException {
    log.info("로그인 시도");
    try {
      // ObjectMapper().readValue(): Jackson 라이브러리의 주요 메서드로, JSON 데이터를 Java 객체로 변환하는 데 사용(역직렬화)
      // readValue(request의 Body부분의 username,password를 Json형식으로 받는다, 변환할 Object Type)
      LoginRequestDto requestDto = new ObjectMapper().readValue(
              request.getInputStream(), LoginRequestDto.class
      );

      /* 1. 사용자가 로그인을 시도한 Json데이터(username,password)를 가지고
      *  2. 인증 객체인 UsernamePasswordAuthenticationToken을 만든다
      *  3. 만든Token 정보를 AuthenticationManager가 .authenticate()를 통하여 인증 성공, 실패를 판단한다 */
      return getAuthenticationManager().authenticate(
              // authenticate(): 인증을 처리하는 메서드
              new UsernamePasswordAuthenticationToken(
                      // 사용자가 로그인을 시도한 데이터인 username과 passowrd
                      requestDto.getUsername(),
                      requestDto.getPassword(),
                      null
              )
      );
    } catch (IOException e) {
      log.error(e.getMessage());
      throw new RuntimeException(e.getMessage());
    }
  }

  @Override
  // AuthenticationManager가 로그인 성공시 보내주는 곳
  protected void successfulAuthentication(
          HttpServletRequest request,
          HttpServletResponse response,
          FilterChain chain,
          // SecurityContextHolder/SecurityContext/Authentication/Principal
          Authentication authResult // 인증 성공한 정보를 바탕으로 여기에 사용자 정보를 담는 UserDetails가 담긴다
  ) throws IOException, ServletException {
    log.info("로그인 성공 및 JWT 생성");
    String username = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();
    UserRoleEnum role = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getRole();

    // 토큰을 생성할 때 사용자 식별 username과 역할을 넣어준다
    String token = jwtUtil.createToken(username, role); // // Bearer eyJhbGciOiJIUzI...
    jwtUtil.addJwtToCookie(token, response);
  }

  @Override
  // AuthenticationManager가 로그인 실패시 보내주는 곳
  protected void unsuccessfulAuthentication(
          HttpServletRequest request,
          HttpServletResponse response,
          AuthenticationException failed
  ) throws IOException, ServletException {
    log.info("로그인 실패");
    // 401 Error: Unauthorized(인증 실패)
    response.setStatus(401);
  }
}