package com.sparta.springauth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDto {
  @NotBlank // 유효성 검사, 검증하기 위해 사용
  private String username;
  @NotBlank
  private String password;
  // @Email // 이메일 형식의 주소를 받는다
  /*
   정규 표현식 사용해보기: Email을 @Pattern()사용하여 표현
   regexp: 검증(Validation)에 사용될 정규 표현식을 지정
   ^: 문자열의 시작을 의미 / $: 끝을 의미
   (.+): 하나 이상의 문자를 의미
   [ㄱ-ㅎ가-힣a-zA-Z0-9]: 한글 영어 숫자
   \s: 공백 허용  ex) [ㄱ-ㅎ가-힣a-zA-Z0-9\s]: 한글 영어 숫자 공백
   */
  // @Pattern(regexp = "^(.+)@(.+)$") // "@" 앞 뒤로 하나 이상의 문자가 들어와야 한다
  //  @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{3,20}$") // 한글, 영어, 숫자, _언더바, -하이픈
  @Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
  @NotBlank
  private String email;
  private boolean admin = false;
  private String adminToken = "";
}