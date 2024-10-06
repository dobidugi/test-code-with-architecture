package com.example.demo.repository;

import com.example.demo.model.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Test
  void UserRepository_가_재대로_연결되었다() {
    //given
    UserEntity userEntity = new UserEntity();
    userEntity.setEmail("dobidugi@gmail.com");
    userEntity.setNickname("dobidugi");
    userEntity.setAddress("서울시 강남구");
    userEntity.setStatus(UserStatus.ACTIVE);
    userEntity.setCertificationCode("aaaaaa-aaaaa-aaaaa-aaaaa-aaaaa");

    //when
    userRepository.save(userEntity);

    //then
    assertThat(userRepository.findById(userEntity.getId())).isNotEmpty();
  }

  @Test
  void findByIdAndUserStatus_로_유저_데이터를_찾아올_수_있다() {
    //given
    UserEntity userEntity = new UserEntity();
    userEntity.setEmail("dobidugi@gmail.com");
    userEntity.setNickname("dobidugi");
    userEntity.setAddress("서울시 강남구");
    userEntity.setStatus(UserStatus.ACTIVE);
    userEntity.setCertificationCode("aaaaaa-aaaaa-aaaaa-aaaaa-aaaaa");

    //when
    userRepository.save(userEntity);
    Optional<UserEntity> findUser = userRepository.findByIdAndStatus(userEntity.getId(), UserStatus.ACTIVE);

    //then
    assertThat(findUser.isPresent()).isTrue();
  }

  @Test
  void findByIdAndUserStatus_로_유저_데이터를_찾아올_수_없다면_Optional_empty_를_내려준다() {
    //given
    UserEntity userEntity = new UserEntity();
    userEntity.setEmail("dobidugi@gmail.com");
    userEntity.setNickname("dobidugi");
    userEntity.setAddress("서울시 강남구");
    userEntity.setStatus(UserStatus.ACTIVE);
    userEntity.setCertificationCode("aaaaaa-aaaaa-aaaaa-aaaaa-aaaaa");

    //when
    userRepository.save(userEntity);
    Optional<UserEntity> findUser = userRepository.findByIdAndStatus(userEntity.getId(), UserStatus.PENDING);

    //then
    assertThat(findUser.isEmpty()).isTrue();
  }

  @Test
  void findByEmailAndStatus_로_우저_데이터를_찾아올_수_있다() {
    //given
    UserEntity userEntity = new UserEntity();
    userEntity.setEmail("dobidugi@gmail.com");
    userEntity.setNickname("dobidugi");
    userEntity.setAddress("서울시 강남구");
    userEntity.setStatus(UserStatus.ACTIVE);
    userEntity.setCertificationCode("aaaaaa-aaaaa-aaaaa-aaaaa-aaaaa");

    //when
    userRepository.save(userEntity);
    Optional<UserEntity> findUser = userRepository.findByEmailAndStatus("dobidugi@gmail.com", UserStatus.ACTIVE);

    //then
    assertThat(findUser.isPresent()).isTrue();
  }

  @Test
  void findByEmailAndStatus_로_유저_데이터를_찾아올_수_없다면__Optional_empty_를_내려준() {
    //given
    UserEntity userEntity = new UserEntity();
    userEntity.setEmail("dobidugi@gmail.com");
    userEntity.setNickname("dobidugi");
    userEntity.setAddress("서울시 강남구");
    userEntity.setStatus(UserStatus.ACTIVE);
    userEntity.setCertificationCode("aaaaaa-aaaaa-aaaaa-aaaaa-aaaaa");

    //when
    userRepository.save(userEntity);
    Optional<UserEntity> findUser = userRepository.findByEmailAndStatus("dobidugi@gmail.com", UserStatus.PENDING);

    //then
    assertThat(findUser.isEmpty()).isTrue();
  }
}