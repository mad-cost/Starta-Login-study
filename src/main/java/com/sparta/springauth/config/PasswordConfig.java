package com.sparta.springauth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordConfig {

  @Bean // Spring서버가 뜰 때 IocContainer에 메서드가 Bean으로 같이 등록된다
  public PasswordEncoder passwordEncoder(){
    // BCryptPasswordEncoder: PasswordEncoder의 구현체 -> 비밀번호를 암호화 하기 위해 사용
    return new BCryptPasswordEncoder();
  }

}
