package com.sparta.springauth.entity;

public enum UserRoleEnum {
  USER(Authority.USER),  // 사용자 권한
  ADMIN(Authority.ADMIN);  // 관리자 권한

  private final String authority;


  UserRoleEnum(String authority) {
    this.authority = authority;
  }

  public String getAuthority() {
    return this.authority;
  }

  public static class Authority { // 권한 상수를 담고 있는 클래스
    // 권한 이름 규칙: "ROLE"로 시작해야 한다 / UserDetailsImpl의 @Override에서 사용자의 권한 부여 설정
    public static final String USER = "ROLE_USER";
    public static final String ADMIN = "ROLE_ADMIN";
  }
}