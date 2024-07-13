package com.sparta.springauth.jwt;

import com.sparta.springauth.entity.UserRoleEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;
import java.util.Base64;
import java.util.Date;


@Component
// Util: 특정한 매개변수, 파라미터에 대한 작업을 수행하는 메서드가 존재하는 클래스
//       즉, 다른객체에 의존하지 않고, 하나의 모듈로써 동작하는 클래스 ex) 날짜, 원, (3,000)과 같이 문자열을 조작하는 Util 클래스
public class JwtUtil {

// 1. JWT 설정 데이터 만들기
  // Header KEY 값 (Cookie 형태의 Name값) {Name : Vale}
  public static final String AUTHORIZATION_HEADER = "Authorization";
  // 사용자의 권한을 가져오기 위한 Key값 -> ex) User, admin ..
  public static final String AUTHORIZATION_KEY = "auth";
  // Token 식별자 -> Bearer(공백 1칸)
  public static final String BEARER_PREFIX = "Bearer ";
  // 토큰 만료시간
  private final long TOKEN_TIME = 60 * 60 * 1000L; // 60분

  // beans.factory.annotation.Value
  @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
  private String secretKey;
  private Key key; // secretKey를 담을 Key객체  -> Jwt에서 secretKey를 관리하는 방법은 여러가지가 있다
  private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256; // algo는 HS256방식

  // 로그 설정 / 로깅: Application이 동작할 때 프로젝트의 상태, 동작 정보를 시간순으로 기록한다.
  public static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그");

  @PostConstruct // 초기화 작업 수행: 한 번만 받아오면 되는 값을 사용할 때마다, 요청을 새로 호출하는 실수를 방지
  // secretKey는 Base64로 한 번만 디코딩하면 된다.
  public void init() {
    // Base64로 인코딩된 secretKey를 디코딩하여 원래의 데이터 형태로 변환
    byte[] bytes = Base64.getDecoder().decode(secretKey); // 반환 타입: 'byte[]'로 받게 된다
    // 디코딩된 bytes를 이용하여 HMAC SHA 키 객체를 생성
    key = Keys.hmacShaKeyFor(bytes);
  }

// 2. JWT 생성
  // 토큰 생성
  public String createToken(String username, UserRoleEnum role) {
    Date date = new Date(); // 현재 시간

    // Bearer eyJhbGciOiJIUzI...
    return BEARER_PREFIX +
            Jwts.builder()
                    .setSubject(username) // 사용자 식별자값(ID)
                    .claim(AUTHORIZATION_KEY, role) // 사용자 권한 {Key : Value} 형식
                    .setExpiration(new Date(date.getTime() + TOKEN_TIME)) // 만료 기한: 현재 시간 + 만료 시간
                    .setIssuedAt(date) // 발급일
                    // signWith(secretKey ,서명에 사용되는 algo): secretKey와 algo에 의한 데이터 암호화
                    .signWith(key, signatureAlgorithm) // builder()에서 만든 데이터를 암호화 한다
                    .compact();
  }

// 3. 생성된 JWT를 Cookie객체에 저장
  // JWT Cookie 에 저장
  public void addJwtToCookie(String token, HttpServletResponse res) {
    // AuthController의 static addCookie() / 쿠키가 생성되는 과정 참조
    try {
      // URLEncoder.encode(): Cookie Value 에는 공백이 불가능 하기 때문에 URLEncoder.encode()을 사용하여 해결
      // token에는 공백이 있으면 안된다, token을 URLEncoder로 encode해준다: Bearer eyJhbGciOi... -> Bearer%20eyJhbGciOi...
      token = URLEncoder.encode(token, "utf-8").replaceAll("\\+", "%20"); // Bearer%20eyJhbGciOi...

      // @@ 생성한 JWT를 Cookie에 담아준다
      // servlet.http.Cookie
      // 쿠키 스토리지에 담기는 형태 {"Name" : "Authorization" : "Key" : "Bearer%20eyJhbGciOi..."}
      Cookie cookie = new Cookie(AUTHORIZATION_HEADER, token); // {Name : Value} (F12 쿠키 스토리지의 쿠키 형태 참조)
      cookie.setPath("/"); // (F12 쿠키 스토리지에 있는 쿠키 형태 속의 Path)

      // @@ JWT를 담은 Cookie를 쿠키 스토리지에 담아준다
      // Response 객체에 데이터를 담으면 클라이언트(쿠키 스토리지)에 자동으로 반환이 가능하다
      // 쿠키 스토리지에 담기는 형태 {"Name" : "Authorization" : "Key" : "Bearer%20eyJhbGciOi..."}
      res.addCookie(cookie); // addCookie(): 이미 존재하는 HttpServletResponse(res).addCookie()를 사용하여 Response에 데이터 담아주기
    } catch (UnsupportedEncodingException e) {
      logger.error(e.getMessage());
    }
  }

// 4. Cookie에 들어있던 JWT 토큰을 Substring
  // JWT 토큰 substring
  public String substringToken(String tokenValue) { // tokenValue: Bearer eyJhbGciOi...
    // StringUtils.hasText(): 공백인지 or Null인지 확인 && startsWith(): BEARER_PREFIX = Bearer(공백)으로 시작하는지?
    if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
      return tokenValue.substring(7); // 순수한 토큰 값 = substring(7): 앞에 7글자를 짜른다 -> Bearer(공백 1칸)
    }
    logger.error("Not Found Token"); // 토큰이 없을 경우
    throw new NullPointerException("Not Found Token");
  }

// 5. JWT 검증
  // 토큰 검증
  public boolean validateToken(String token) { // token: eyJhbGciOi...
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token); // 토큰 검증
      return true; // 검증된 토큰

    } catch (SecurityException | MalformedJwtException | SignatureException e) {
      logger.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
    } catch (ExpiredJwtException e) {
      logger.error("Expired JWT token, 만료된 JWT token 입니다.");
    } catch (UnsupportedJwtException e) {
      logger.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
    } catch (IllegalArgumentException e) {
      logger.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
    }
    return false;
  }

// 6. JWT에서 사용자 정보 가져오기
  // 토큰에서 사용자 정보 가져오기
  public Claims getUserInfoFromToken(String token) {
    // 반환 값: Claims -> Jwt는 Claim기반 웹 토큰이다
    // getUserInfoFromToken() 클래스: 검증된 토큰일 경우에 사용하는데, Body부분의 Claims를 가져올 수 있다 = 사용자 정보 꺼내기
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
  }


  // HttpServletRequest 에서 Cookie의 Value: JWT 가져오기 / {"Value" : "Barer20%.."}
  // 이렇게 직접 만들어주는 이유는 Filter는 Spring보다 먼저 실행되기 때문 / Controller에서 처럼 @CookieValue 사용 불가
  public String getTokenFromRequest(HttpServletRequest req) {
    // 쿠키 스토리지에 담겨있는 Cookie 의 형태 {"Authorization" : "Bearer%20eyJhbGci..."}
    Cookie[] cookies = req.getCookies(); // Cookie[]: 쿠키 스토리지(Response)에서 모든 쿠키 꺼내기
    if(cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals(AUTHORIZATION_HEADER)) {
          try {
            // 토큰에 문제가 없다면 디코딩 후 반환: Bearer%20eyJhbGci -> Bearer eyJhbGci...
            return URLDecoder.decode(cookie.getValue(), "UTF-8"); // Encode 되어 넘어간 Value 다시 Decode
          } catch (UnsupportedEncodingException e) {
            return null;
          }
        }
      }
    }
    return null;
  }

}