package com.sparta.springauth.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j(topic = "LoggingFilter")
@Component
// Filter는 여러개가 있는데 이것을 Filter-Chain 이라고 한다
@Order(1) // Filter 동작 순서 지정 / 첫 번째로 동작하는 Filter
// 클라이언트에서 요청이 들어오면 httpServlet이 동작하기 전에 Filter가 먼저 동작된다
public class LoggingFilter implements Filter {
  @Override
  // Filter 인터페이스의 doFilter()사용하여 재정의 해준다
  public void doFilter(
          ServletRequest request, // URL 정보를 가져온다
          ServletResponse response,
          FilterChain chain // Filter 를 이동할 때 사용
  ) throws IOException, ServletException {
    // 전처리
    HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    String url = httpServletRequest.getRequestURI(); // request에서 받아온 URL정보
    log.info(url);

    chain.doFilter(request, response); // 다음 Filter 로 이동 (AuthFilter)

    // 후처리
    log.info("비즈니스 로직 완료");
  }
}