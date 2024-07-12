package com.sparta.springauth.auth;

import com.sparta.springauth.entity.UserRoleEnum;
import com.sparta.springauth.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

  public static final String AUTHORIZATION_HEADER = "Authorization";

  private final JwtUtil jwtUtil;

// -- Cookie --

  // 쿠키를 생성하는 방법
  @GetMapping("/create-cookie")
  public String createCookie(HttpServletResponse res) {
    // addCookie: 쿠키를 만들어 주는 메서드 / 아래 static addCookie메서드 참조
    addCookie("Jun Auth", res);

    return "createCookie"; // RestController -> createCookie를 View에 반환하면 성공
  }

  // 쿠키를 가져오는 방법
  @GetMapping("/get-cookie")
  public String getCookie(
          // @CookieValue(가지고 오려는 쿠키 Name 값)를 이용하여 쿠키를 가져온다
          @CookieValue(AUTHORIZATION_HEADER) // Authorization이라는 Name의 쿠키 Value를 가져온다
          String value // Authorization의 Value값
  ) {
    System.out.println("value = " + value); // value = Jun Auth

    return "getCookie : " + value; // getCookie : Jun Auth
  }


//  -- Session --

  // 세션 데이터 만들기 {"Name" : "SESSIONID", "Value" : "6ASFT15RHSA..(난수)"
  @GetMapping("/create-session")
  public String createSession(HttpServletRequest req) {
    // 세션이 존재할 경우 세션 반환, 없을 경우 새로운 세션을 생성한 후 반환
    HttpSession session = req.getSession(true);

    // 없을 경우: setAttribute(Name: Value)를 사용하여 세션에 저장될 쿠키 정보 생성.
    session.setAttribute(AUTHORIZATION_HEADER, "Jun Auth");

    return "createSession";
  }

  // 세션 데이터 가져오기
  @GetMapping("/get-session")
  public String getSession(HttpServletRequest req) {
    // 세션이 존재할 경우 세션 반환, 없을 경우 null 반환
    HttpSession session = req.getSession(false);
    // /create-session에서 session.setAttribute()를 통해 세션을 만들어 놓았다, null일 경우 if문을 통해 제어 가능

    // getAttribute(Name): Name에 해당하는 Value를 가져온다
    // {"Name" : "Authorization", "Value" : "Jun Auth"}
    String value = (String) session.getAttribute(AUTHORIZATION_HEADER);
    System.out.println("value = " + value); // value = Jun Auth

    return "getSession : " + value;
  }


//  -- Jwt --

  // Jwt 토큰 생성
  @GetMapping("/create-jwt")
  public String createJwt(HttpServletResponse res) {
    // Jwt 생성
    String token = jwtUtil.createToken("Jun", UserRoleEnum.USER); // token: Bearer eyJhbGciOi...

    // 생성된 JWT를 Cookie스토리지에 저장
    jwtUtil.addJwtToCookie(token, res); // {"Name" : "Authorization" : "Key" : "Bearer%20eyJhbGciOi..."}

    return "createJwt : " + token; // createJwt : Bearer eyJhbGciOi...
  }

  // Jwt 토큰 조회: 검증된 토큰의 사용자 정보 조회
  @GetMapping("/get-jwt")
  public String getJwt(
          // {"Name" : "Authorization" : "Key" : "Bearer%20eyJhbGciOi..."}
          // @CookieValue(가지고 오려는 쿠키 Name 값)를 이용하여 쿠키를 가져온다
          @CookieValue(JwtUtil.AUTHORIZATION_HEADER)
          String tokenValue // Authorization의 Value값: Bearer eyJhbGciOi...
  ) {
    log.info("@@@@ : {}", tokenValue); // Bearer eyJhbGciOi...
    // JWT 토큰 substring: 순수한 토큰 값 찾기
    String token = jwtUtil.substringToken(tokenValue); // eyJhbGciOi...

    // 토큰 검증
    if(!jwtUtil.validateToken(token)){ // jwtUtil.validateToken()이 false를 반환해야 true가 되므로 if문 실행
      throw new IllegalArgumentException("Token Error");
    }

    // if문을 통하여 검증된 토큰에서 사용자 정보 가져오기
    Claims info = jwtUtil.getUserInfoFromToken(token);
    // 사용자 username
    String username = info.getSubject();
    System.out.println("username = " + username);
    // 사용자 권한: /create-jwt의 createToken()에서 권한을 Key : Value형식으로 넣어 줬다
    // 즉, info.get()을 통하여 검증된 토큰 Key값의 Value값을 찾을 수 있다
    String authority = (String) info.get(JwtUtil.AUTHORIZATION_KEY);
    System.out.println("authority = " + authority);

    return "getJwt : " + username + ", " + authority; // getJwt : Jun, USER
  }


  // 쿠키가 생성되는 과정
  public static void addCookie(String cookieValue, HttpServletResponse res) {
    try {
      // URLEncoder.encode(): Cookie Value 에는 공백이 불가능 하기 때문에 URLEncoder.encode()을 사용하여 해결
      cookieValue = URLEncoder.encode(cookieValue, "utf-8").replaceAll("\\+", "%20"); // "\\+"(공백)을 "%20"로 바꿔준다 -> Jun%20Auth
      // 공백을 바꿔준 cookieValue값(Jun%20Auth)을 Cookie에 새로 담아준다
      // {"Name" : "Authorization", "Value" : "Jun%20Auth"}
      Cookie cookie = new Cookie(AUTHORIZATION_HEADER, cookieValue); // Name-Value형식 (F12 쿠키 스토리지의 쿠키 형태 참조)
      cookie.setPath("/"); // (F12 쿠키 스토리지에 있는 쿠키 형태 속의 Path)
      cookie.setMaxAge(30 * 60); // 만료 기한: 30(분) * 60(초) = 1800초 -> 30분
//    ▲ 위에서 만들어준 쿠키를 / ▼ 아래의 HttpServletResponse(res)에 담아준다.
      // Response 객체에 데이터를 담으면 클라이언트에 자동으로 반환이 가능하다
      res.addCookie(cookie); // addCookie(): 이미 존재하는 HttpServletResponse(res).addCookie()를 사용하여 Response에 데이터 담아주기
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e.getMessage());
    }
  }
}