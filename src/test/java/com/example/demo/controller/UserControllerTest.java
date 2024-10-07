package com.example.demo.controller;

import com.example.demo.model.UserStatus;
import com.example.demo.model.dto.UserUpdateDto;
import com.example.demo.repository.UserEntity;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@SqlGroup(
    {
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/sql/user-service-test-data.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/sql/delete-all-data.sql")
    }

)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserService userService;

  ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void 유저_아이디를_통해_유저_정보를_가져올_수_있다() throws Exception {
    //given


    //then
    //when
    mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.email").value("dobidugi@gmail.com"));
  }


  @Test
  public void pending_상태인_유저는_certificationCode를_이용해_Active_상태로_만들_수_있다() throws Exception {
    //given

    //when
    //then
    mockMvc.perform(get("/api/users/"+"2"+"/verify")
                    .param("certificationCode", "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab"))
            .andExpect(status().isFound());

    UserEntity verifyUser = userService.getById(2L);
    assertEquals(verifyUser.getStatus(), UserStatus.ACTIVE);
  }

  @Test
  public void 내_정보를_조회하면_주소를_가져올_수_있다() throws Exception {
    //given

    //when
    //then
    mockMvc.perform(get("/api/users/me")
                    .header("EMAIL", "dobidugi@gmail.com")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.address").value("Seoul"));
  }

  @Test
  public void 내_정보를_수정할_수_있다() throws Exception {
    //given
    UserUpdateDto userUpdateDto = UserUpdateDto.builder()
            .address("Inc")
            .build();
    //when
    //then
    mockMvc.perform(put("/api/users/me")
                    .header("EMAIL", "dobidugi@gmail.com")
                    .header("Content-type", "application/json")

                    .content(objectMapper.writeValueAsString(userUpdateDto))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.address").value("Inc"));
    UserEntity findUser = userService.getById(1L);
    assertThat(findUser.getAddress()).isEqualTo("Inc");
  }



}