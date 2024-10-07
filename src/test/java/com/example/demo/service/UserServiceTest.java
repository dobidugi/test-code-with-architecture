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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@TestPropertySource("classpath:application.properties")
@SqlGroup(
        {
                @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/sql/user-service-test-data.sql"),
                @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/sql/delete-all-data.sql")
        }
)
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
    String email = "dobidugi@gmail.com";

    //when
    UserEntity findUser = userService.getByEmail(email);

    //then
    assertThat(findUser.getEmail()).isEqualTo(email);

  }


  @Test
  void getByEmail은_Active_상태가_아니라면_에러를_발생_시킨다() {
    // given
    String email = "asd3@naver.com";

    //when
    //then
    assertThatThrownBy(() -> {
      userService.getByEmail(email);
    }).isInstanceOf(ResourceNotFoundException.class);

  }

  @Test
  void getById는_Active_상태의_유저를_가져온다() {
    // given

    //when
    UserEntity findUser = userService.getById(1L);

    //then
    assertThat(findUser.getEmail()).isEqualTo("dobidugi@gmail.com");

  }


  @Test
  void getById는_Active_상태가_아니라면_에러를_발생_시킨다() {
    // given

    //when
    //then
    assertThatThrownBy(() -> {
      userService.getById(2L);
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
    //given

    UserUpdateDto updateDto = UserUpdateDto.builder()
            .nickname("dobidugi2")
            .address("인천시")
            .build();


    //when
    UserEntity updateUser = userService.update(1L, updateDto);


    //then
    UserEntity findUser = userService.getById(1L);
    assertThat(findUser.getId()).isNotNull();
    assertThat(findUser.getNickname()).isEqualTo(updateDto.getNickname());
    assertThat(findUser.getAddress()).isEqualTo(updateDto.getAddress());
  }

  @Test
  void user를_로그인_시키면_마지막_로그인_시간이_변경된다() {
    //given

    //when
    userService.login(1L);

    //then
    UserEntity userEntity = userService.getById(1);
    assertThat(userEntity.getLastLoginAt()).isNotNull();
    assertThat(userEntity.getLastLoginAt()).isGreaterThan(0L);
  }

  @Test
  void PENDING_상태의_사용자는_인증코드로_ACTIVE_로_변경할_수_있다() {
    //given

    //when
    userService.verifyEmail(2L, "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab");

    //then
    UserEntity findUser = userService.getById(2L);
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