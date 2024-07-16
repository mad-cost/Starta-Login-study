package com.sparta.springauth.controller;

import com.sparta.springauth.dto.LoginRequestDto;
import com.sparta.springauth.dto.SignupRequestDto;
import com.sparta.springauth.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/api")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/user/login-page")
  public String loginPage() {
    return "login";
  }

  // 회원 가입
  @GetMapping("/user/signup")
  public String signupPage() {
    return "signup";
  }

  @PostMapping("/user/signup")
  public String signup(
          @Valid // SignupRequestDto객체 유효성 검사
          // @ModelAttribute 가 생략되어 있다 / HTML <form> 데이터를 DTO 객체로 매핑
          SignupRequestDto requestDto,
          // BindingResult: SignupRequestDto객체가 유효성 검사에 실패할 경우, BindingResult에 오류 정보를 담는다
          BindingResult bindingResult
  ) {
    // Validation 예외처리
    List<FieldError> fieldErrors = bindingResult.getFieldErrors();
    if(fieldErrors.size() > 0) {
      for (FieldError fieldError : bindingResult.getFieldErrors()) {
        log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());
      }
      // 회원 가입 실패시, 다시 회원 가입 페이지로 반환
      return "redirect:/api/user/signup";
    }

    userService.signup(requestDto);
    // 회원 가입에 성공시, 로그인 페이지로 반환
    return "redirect:/api/user/login-page";
  }
}