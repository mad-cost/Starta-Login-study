package com.sparta.springauth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class PasswordEncoderTest {

  @Autowired
  PasswordEncoder passwordEncoder;

  @Test
  @DisplayName("수동 등록한 passwordEncoder를 주입 받아와 문자열 암호화")
  // 입력된 비밀번호와 암호화된 비밀번호가 일치하는지 비교하기
  void test1() {
    String password = "Jun's password";

    // 암호화
    String encodePassword = passwordEncoder.encode(password);
    System.out.println("encodePassword = " + encodePassword); // 암호화된 값 출력

    String inputPassword = "Jun";

    // 입력된 비밀번호와 암호화된 비밀번호가 일치하는지 비교: matches(평문데이터, 암호화된 데이터)
    boolean matches = passwordEncoder.matches(inputPassword, encodePassword);
    System.out.println("matches = " + matches); // 암호화할 때 사용된 값과 다른 문자열과 비교했기 때문에 false
  }



}