package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.UserStatus;
import com.example.demo.model.dto.PostCreateDto;
import com.example.demo.model.dto.PostUpdateDto;
import com.example.demo.repository.PostEntity;
import com.example.demo.repository.UserEntity;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PostServiceTest {

  @Autowired
  private PostService postService;

  @Autowired
  private UserRepository userRepository;

  @Test
  public void getById는_존재하는_게시글을_가져온다() {
    //given
    UserEntity userEntity = new UserEntity();
    userEntity.setEmail("dobidugi@gmail.com");
    userEntity.setNickname("dobidugi");
    userEntity.setAddress("서울시 강남구");
    userEntity.setStatus(UserStatus.ACTIVE);
    userRepository.save(userEntity);


    PostCreateDto postCreateDto = new PostCreateDto(userEntity.getId(), "content");
    PostEntity postEntity = postService.create(postCreateDto);

    //when
    PostEntity findPost = postService.getById(postEntity.getId());

    //then
    assertThat(findPost.getId()).isEqualTo(postEntity.getId());
  }
  
  @Test
  public void getById는_게시글이_존재하지_않을때_에러를_발생시킨다() {

    //given
    final long postId = 1L;

    //when
    //then
    assertThatThrownBy(() -> {
      postService.getById(postId);
    }).isInstanceOf(ResourceNotFoundException.class);
  }
  

  @Test
  public void create는_게시글을_작성_할_수_있다() {
    //given
    UserEntity userEntity = new UserEntity();
    userEntity.setEmail("dobidugi@gmail.com");
    userEntity.setNickname("dobidugi");
    userEntity.setAddress("서울시 강남구");
    userEntity.setStatus(UserStatus.ACTIVE);
    userRepository.save(userEntity);


    PostCreateDto postCreateDto = new PostCreateDto(userEntity.getId(), "content");

    //when
    PostEntity postEntity = postService.create(postCreateDto);


    //then
    PostEntity findPost = postService.getById(postEntity.getId());
    assertThat(findPost.getId()).isEqualTo(postEntity.getId());
  }

  @Test
  public void update는_게시글을_수정_할_수_있다() {
    //given
    UserEntity userEntity = new UserEntity();
    userEntity.setEmail("dobidugi@gmail.com");
    userEntity.setNickname("dobidugi");
    userEntity.setAddress("서울시 강남구");
    userEntity.setStatus(UserStatus.ACTIVE);
    userRepository.save(userEntity);

    PostCreateDto postCreateDto = new PostCreateDto(userEntity.getId(), "content");
    PostEntity postEntity = postService.create(postCreateDto);

    PostUpdateDto postUpdateDto = new PostUpdateDto("update content");

    //when
    postService.update(postEntity.getId(), postUpdateDto);;

    //then
    PostEntity findPost = postService.getById(postEntity.getId());
    assertThat(findPost.getId()).isEqualTo(postEntity.getId());
    assertThat(findPost.getContent()).isEqualTo("update content");
  }

  @Test
  public void update시_게시글을_찾을_수_없다면_에러를_발생_시킨다() {
    //given
    final long postId = 1L;
    PostUpdateDto postUpdateDto = new PostUpdateDto("update content");

    //when
    //then
    assertThatThrownBy(() -> {
      postService.update(postId, postUpdateDto);
    }).isInstanceOf(ResourceNotFoundException.class);
  }


}