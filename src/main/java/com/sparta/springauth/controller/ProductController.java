package com.sparta.springauth.controller;

import com.sparta.springauth.entity.User;
import com.sparta.springauth.entity.UserRoleEnum;
import com.sparta.springauth.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
// AuthFilter 가 잘 동작하는지 보기 위해 만듬
public class ProductController {

  @GetMapping("/products")
  public String getProducts(
          // Authentication의 Principle에 들어있는 userDetails 꺼내기.
          @AuthenticationPrincipal UserDetailsImpl userDetails
          ) {


    /* 사용자 정보 꺼내기: UserDetailsServiceImpl에서 loadUserByUsername()로 유저를 조회하고
       조회 성공 유저를 UserDetailsImpl에 전달, 전달된 유저를 바탕으로 getUser()를 사용 하여 유저 정보 꺼내기 */
    User user = userDetails.getUser();
    System.out.println("user.getUsername() = " + user.getUsername());
    System.out.println("user.getEmail() = " + user.getEmail());

    return "redirect:/";
  }

  @Secured(UserRoleEnum.Authority.ADMIN) // 관리자만 접근이 가능하다
  @GetMapping("/products/secured")
  public String getProductsByAdmin(@AuthenticationPrincipal UserDetailsImpl userDetails) {
    System.out.println("userDetails.getUsername() = " + userDetails.getUsername());
    for (GrantedAuthority authority : userDetails.getAuthorities()) {
      System.out.println("authority.getAuthority() = " + authority.getAuthority());
    }

    return "redirect:/";
  }
}