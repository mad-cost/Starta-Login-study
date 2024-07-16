package com.sparta.springauth.security;

import com.sparta.springauth.entity.User;
import com.sparta.springauth.entity.UserRoleEnum;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class UserDetailsImpl implements UserDetails {

  private final User user;

  // 기본 생성자
  // UserDetailsServiceImpl의 loadUserByUsername(String username)에 유저가 존재한다면 해당 유저를 담아준다
  public UserDetailsImpl(User user) {
    this.user = user;
  }

  // @Getter
  public User getUser() {
    return user;
  }

  @Override
  public String getPassword() {
    return user.getPassword();
  }

  @Override
  public String getUsername() {
    return user.getUsername();
  }

  @Override // 사용자의 권한 부여 설정
  // WebSecurityConfig클래스에서 @Secured사용하기 위한 @EnableGlobalMethodSecurity 추가
  // API 권한 제어 방법: @Controller에서 @Secured로 API 접근 권한 설정이 가능하다
  public Collection<? extends GrantedAuthority> getAuthorities() {
    UserRoleEnum role = user.getRole(); // User / User 객체의 역할(Role)을 가져온다
    String authority = role.getAuthority(); // ROLE_USER / 역할에 해당하는 권한을 가져온다

    SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
    Collection<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(simpleGrantedAuthority);

    return authorities;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}