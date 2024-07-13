package com.sparta.springauth.controller;

import com.sparta.springauth.dto.LoginRequestDto;
import com.sparta.springauth.dto.SignupRequestDto;
import com.sparta.springauth.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
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

  @PostMapping("/user/login")
  public String login(
          // @ModelAttribute 생략
          LoginRequestDto requestDto,
          // Jwt를 Cookie에 담고 Response객체에 넣어주기 위해
          HttpServletResponse res
  ){

    // login()에 문제가 있을경우 login-page로 이동 후 <script>를 사용하여 에러 메세지 보여주기
    try {
      /* requestDto를 사용하여 사용자를 검증하고,
      res를 사용하여 JWT 생성 및 쿠키에 저장 후 Response 객체(쿠키 스토리지)에 추가
      */
      userService.login(requestDto, res);
    } catch (Exception e) {
      return "redirect:/api/user/login-page?error";
    }

    return "redirect:/";
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
          SignupRequestDto requestDto
  ){
      userService.signup(requestDto);
      return "redirect:/api/user/login-page";
  }

}