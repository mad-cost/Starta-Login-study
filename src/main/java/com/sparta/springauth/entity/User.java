package com.sparta.springauth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // null: X, 중복: X
  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  @Enumerated(value = EnumType.STRING) // Enum타입을 DB 컬럼에 저장할 때 사용
  private UserRoleEnum role;

  public User(String username, String password, String email, UserRoleEnum role) {
    this.username = username;
    this.password = password;
    this.email = email;
    this.role = role;
  }
}