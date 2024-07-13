package com.sparta.springauth.service;

import com.sparta.springauth.dto.LoginRequestDto;
import com.sparta.springauth.dto.SignupRequestDto;
import com.sparta.springauth.entity.User;
import com.sparta.springauth.entity.UserRoleEnum;
import com.sparta.springauth.jwt.JwtUtil;
import com.sparta.springauth.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;



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

  public void login(LoginRequestDto requestDto, HttpServletResponse res) {
    String username = requestDto.getUsername();
    String password = requestDto.getPassword();

    // 사용자 아이디 확인
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("등록된 사용자가 없습니다."));

    // 사용자 비밀번호 확인
    //matches(평문 데이터, 암호화된 데이터)
    if (!passwordEncoder.matches(password, user.getPassword())){
      throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }
    // ▲ 사용자의 아이디 비밀번호 인증 성공

    /* ▼ 인증된 사용자의 정보를 바탕으로,
    JWT 생성 및 생성된 JWT를 쿠키에 저장 후 Response 객체(쿠키 스토리지)에 추가
     */
    // Bearer eyJhbGciOiJIUzI...
    String token = jwtUtil.createToken(user.getUsername(), user.getRole());
    // Bearer%20eyJhbGciOiJIUzI...
    jwtUtil.addJwtToCookie(token, res);

  }
}