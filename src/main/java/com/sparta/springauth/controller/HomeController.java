package com.sparta.springauth.controller;

import com.sparta.springauth.security.UserDetailsImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

  // 메인 페이지로 이동
  @GetMapping("/")
  public String home(
          Model model,
          @AuthenticationPrincipal
          UserDetailsImpl userDetails
          ) {
    model.addAttribute("username", userDetails.getUsername());
    return "index";
  }
}