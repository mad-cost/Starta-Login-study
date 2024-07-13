package com.sparta.springauth.controller;

import com.sparta.springauth.dto.SignupRequestDto;
import com.sparta.springauth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

  private final UserService userService;

  @GetMapping("/user/login-page")
  public String loginPage() {
    return "login";
  }

  // 회원 가입
  @GetMapping("/user/signup")
  public String signupPage() {
    return "signup";
  }

  @PostMapping("user/signup")
  public String signup(
          // @ModelAttribute 가 생략되어 있다 / HTML <form> 데이터를 DTO 객체로 매핑
          // SignupRequestDto는 HTML <form>에서 보내는 'name'속성 값과 일치해야 한다
          SignupRequestDto requestDto,
          Model model
  ){
      userService.signup(requestDto);
      return "redirect:/api/user/login-page";
  }

}