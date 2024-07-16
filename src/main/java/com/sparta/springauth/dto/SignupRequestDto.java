package com.sparta.springauth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDto {
  @NotBlank // 유효성 검사, 검증하기 위해 사용
  private String username;
  @NotBlank
  private String password;
  @Email // 이메일 형식의 주소를 받는다
  @NotBlank
  private String email;
  private boolean admin = false;
  private String adminToken = "";
}