package com.sparta.springauth.security;

import com.sparta.springauth.entity.User;
import com.sparta.springauth.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;


  // 기본 생성자
  public UserDetailsServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }


  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    // 해당 유저가 존재하는지
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Not Found " + username));
    // 존재한다면 반환
    return new UserDetailsImpl(user);
  }
}