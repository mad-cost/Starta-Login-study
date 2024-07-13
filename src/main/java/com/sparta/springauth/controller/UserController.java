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
          SignupRequestDto requestDto,
          Model model
  ){
      userService.signup(requestDto);
      return "redirect:/api/user/login-page";
  }

}