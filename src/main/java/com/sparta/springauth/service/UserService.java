package com.sparta.springauth.service;

import com.sparta.springauth.dto.SignupRequestDto;
import com.sparta.springauth.entity.User;
import com.sparta.springauth.entity.UserRoleEnum;
import com.sparta.springauth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  // ADMIN_TOKEN: 실제로는 이런식으로 사용하지 않고 관리자 페이지를 구현한다
  private final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

  public void signup(SignupRequestDto requestDto) {
    String username = requestDto.getUsername();
    // 비밀번호는 암호화 하여 저장해야 한다
    String password = passwordEncoder.encode(requestDto.getPassword());

    // 회원 중복 확인
    Optional<User> checkUsername = userRepository.findByUsername(username);
    // isPresent(): Optional 클래스에서 제공하는 메서드 / 값이 있으면 true, 없으면 false 반환
    if (checkUsername.isPresent()) {
      throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
    }

    // email 중복확인
    String email = requestDto.getEmail();
    Optional<User> checkEmail = userRepository.findByEmail(email);
    if (checkEmail.isPresent()) {
      throw new IllegalArgumentException("중복된 Email 입니다.");
    }

    // 사용자 ROLE 확인: role에 일반사용자 권한을 넣어둔다
    UserRoleEnum role = UserRoleEnum.USER;
    // isAdmin(): admin의 boolean값을 가져온다
    if (requestDto.isAdmin()) {
      if (!ADMIN_TOKEN.equals(requestDto.getAdminToken())) {
        throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
      }
      // 토큰이 일치할 경우 관리자 권한으로 변경
      role = UserRoleEnum.ADMIN;
    }

    // 사용자 등록
    User user = new User(username, password, email, role);
    userRepository.save(user);
  }
}