package com.example.demo.service;

import com.example.demo.exception.CertificationCodeNotMatchedException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.UserStatus;
import com.example.demo.model.dto.UserCreateDto;
import com.example.demo.model.dto.UserUpdateDto;
import com.example.demo.repository.UserEntity;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  @MockBean
  private JavaMailSender mailSender;

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

  @Test
  void create는_유저를_생성할_수_있다() {
    // given
    UserCreateDto dto = UserCreateDto.builder()
            .email("dobidugi@gmail.com")
            .address("서울시 강남구")
            .nickname("dobidugi")
            .build();

    //when
    UserEntity createUser = userService.create(dto);
    BDDMockito.doNothing().when(mailSender).send(BDDMockito.any(SimpleMailMessage.class));


    //then
    assertThat(createUser.getId()).isNotNull();
    assertThat(createUser.getStatus()).isEqualTo(UserStatus.PENDING);
  }

  @Test
  void update는_유저를_수정할_수_있다() {
    // given
    UserCreateDto dto = UserCreateDto.builder()
            .email("dobidugi@gmail.com")
            .address("서울시 강남구")
            .nickname("dobidugi")
            .build();

    UserEntity createUser = userService.create(dto);
    BDDMockito.doNothing().when(mailSender).send(BDDMockito.any(SimpleMailMessage.class));
    createUser.setStatus(UserStatus.ACTIVE);
    userRepository.flush();

    UserUpdateDto updateDto = UserUpdateDto.builder()
            .nickname("dobidugi2")
            .address("인천시")
            .build();


    //when
    UserEntity updateUser = userService.update(createUser.getId(), updateDto);


    //then
    UserEntity findUser = userRepository.getById(createUser.getId());
    assertThat(findUser.getId()).isNotNull();
    assertThat(findUser.getNickname()).isEqualTo(updateDto.getNickname());
    assertThat(findUser.getAddress()).isEqualTo(updateDto.getAddress());
  }

  @Test
  void user를_로그인_시키면_마지막_로그인_시간이_변경된다() {
    //given
    UserCreateDto dto = UserCreateDto.builder()
            .email("dobidugi@gmail.com")
            .address("서울시 강남구")
            .nickname("dobidugi")
            .build();
    UserEntity createUser = userService.create(dto);
    BDDMockito.doNothing().when(mailSender).send(BDDMockito.any(SimpleMailMessage.class));
    createUser.setStatus(UserStatus.ACTIVE);

    //when
    userService.login(createUser.getId());

    //then
    UserEntity findUser = userRepository.getById(createUser.getId());
    assertThat(findUser.getLastLoginAt()).isNotNull();
    assertThat(findUser.getLastLoginAt()).isGreaterThan(0L);
  }

  @Test
  void PENDING_상태의_사용자는_인증코드로_ACTIVE_로_변경할_수_있다() {
    //given
    UserCreateDto dto = UserCreateDto.builder()
            .email("dobidugi@gmail.com")
            .address("서울시 강남구")
            .nickname("dobidugi")
            .build();
    UserEntity createUser = userService.create(dto);
    BDDMockito.doNothing().when(mailSender).send(BDDMockito.any(SimpleMailMessage.class));
    createUser.setStatus(UserStatus.ACTIVE);
    createUser.setCertificationCode("aaaaaa-aaaaa-aaaaa-aaaaa-aaaaa");

    //when
    userService.verifyEmail(createUser.getId(), "aaaaaa-aaaaa-aaaaa-aaaaa-aaaaa");

    //then
    UserEntity findUser = userRepository.getById(createUser.getId());
    assertThat(findUser.getStatus()).isEqualTo(UserStatus.ACTIVE);
  }

  @Test
  void PENDING_상태의_사용자는_인증코드_실패시_에러를_발생_시킨다() {
    //given
    UserCreateDto dto = UserCreateDto.builder()
            .email("dobidugi@gmail.com")
            .address("서울시 강남구")
            .nickname("dobidugi")
            .build();
    UserEntity createUser = userService.create(dto);
    BDDMockito.doNothing().when(mailSender).send(BDDMockito.any(SimpleMailMessage.class));
    createUser.setStatus(UserStatus.ACTIVE);
    createUser.setCertificationCode("aaaaaa-aaaaa-aaaaa-aaaaa-aaaaa");

    //when
    //then
    assertThatThrownBy(() -> {
      userService.verifyEmail(createUser.getId(), "bbbb-bbbbb-bbbbbb");
    }).isInstanceOf(CertificationCodeNotMatchedException.class);
  }
}