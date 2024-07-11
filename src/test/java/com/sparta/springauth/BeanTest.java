package com.sparta.springauth;

import com.sparta.springauth.food.Food;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BeanTest {

/* 같은 타입의 Bean이 2개일 경우 해결방법 1
  // Food food; -> Error Food에 빈이 2개가 등록 되어 있다

  @Autowired
  Food pizza; // 사용자가 Food에 어떤 구현체를 사용할껀지 명시해 줘야 한다

  @Autowired
  Food chicken;

  @Test
  @DisplayName("구현체를 지정해줘서 사용하기")
  void test1(){
    pizza.eat();
    chicken.eat();
  }
*/

/* 같은 타입의 Bean이 2개일 경우 해결방법 2
  @Autowired
  Food food; // 사용하려는 구현체에 @Primary추가

  @Test
  @DisplayName("chicken 메서드에 @Primary를 추가하여 사용하기")
  void test2(){
    food.eat();
  }
*/

  // 같은 타입의 Bean이 2개일 경우 해결방법 3
  @Autowired
  @Qualifier("pizza") // Pizza구현체에 @Qualifier추가
  Food food; // Chicken구현체에 @Primary를 지우지 않았다

  @Test
  @DisplayName("Primary와 Qualifiler 우선순위 확인하기")
  void test3(){
    food.eat(); // 피자를 먹습니다
  }


}
