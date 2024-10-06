package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.UserStatus;
import com.example.demo.repository.UserEntity;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  @Test
  void getByEmail은_Active_상태의_유저를_가져온다() {
    // given
    UserEntity userEntity = new UserEntity();
    userEntity.setEmail("dobidugi@gmail.com");
    userEntity.setNickname("dobidugi");
    userEntity.setAddress("서울시 강남구");
    userEntity.setStatus(UserStatus.ACTIVE);
    userEntity.setCertificationCode("aaaaaa-aaaaa-aaaaa-aaaaa-aaaaa");
    userRepository.save(userEntity);
    String email = "dobidugi@gmail.com";

    //when
    UserEntity findUser = userService.getByEmail(email);

    //then
    assertThat(findUser.getEmail()).isEqualTo(email);

  }


  @Test
  void getByEmail은_Active_상태가_아니라면_에러를_발생_시킨다() {
    // given
    UserEntity userEntity = new UserEntity();
    userEntity.setEmail("dobidugi@gmail.com");
    userEntity.setNickname("dobidugi");
    userEntity.setAddress("서울시 강남구");
    userEntity.setStatus(UserStatus.PENDING);
    userEntity.setCertificationCode("aaaaaa-aaaaa-aaaaa-aaaaa-aaaaa");
    userRepository.save(userEntity);
    String email = "dobidugi@gmail.com";

    //when
    //then
    assertThatThrownBy(() -> {
      userService.getByEmail(email);
    }).isInstanceOf(ResourceNotFoundException.class);

  }

  @Test
  void getById는_Active_상태의_유저를_가져온다() {
    // given
    UserEntity userEntity = new UserEntity();
    userEntity.setEmail("dobidugi@gmail.com");
    userEntity.setNickname("dobidugi");
    userEntity.setAddress("서울시 강남구");
    userEntity.setStatus(UserStatus.ACTIVE);
    userEntity.setCertificationCode("aaaaaa-aaaaa-aaaaa-aaaaa-aaaaa");
    userRepository.save(userEntity);

    //when
    UserEntity findUser = userService.getById(userEntity.getId());

    //then
    assertThat(findUser.getEmail()).isEqualTo(userEntity.getEmail());

  }


  @Test
  void getById는_Active_상태가_아니라면_에러를_발생_시킨다() {
    // given
    UserEntity userEntity = new UserEntity();
    userEntity.setEmail("dobidugi@gmail.com");
    userEntity.setNickname("dobidugi");
    userEntity.setAddress("서울시 강남구");
    userEntity.setStatus(UserStatus.PENDING);
    userEntity.setCertificationCode("aaaaaa-aaaaa-aaaaa-aaaaa-aaaaa");
    userRepository.save(userEntity);

    //when
    //then
    assertThatThrownBy(() -> {
      userService.getById(userEntity.getId());
    }).isInstanceOf(ResourceNotFoundException.class);

  }
}