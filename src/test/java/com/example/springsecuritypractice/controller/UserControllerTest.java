package com.example.springsecuritypractice.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.example.springsecuritypractice.domian.dto.UserJoinRequest;
import com.example.springsecuritypractice.domian.dto.UserLoginRequest;
import com.example.springsecuritypractice.exception.AppException;
import com.example.springsecuritypractice.exception.ErrorCode;
import com.example.springsecuritypractice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// mock test
@WebMvcTest
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    // java object 를 json 으로 만들어 준다.
    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 성공 테스트")
    @WithMockUser
    void join() throws Exception {
        String userName = "juwan";
        String password = "123qwe";



        mockMvc.perform(post("/api/v1/users/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserJoinRequest(userName, password))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원가입 실패 테스트")
    @WithMockUser
    void join_fail() throws Exception {
        String userName = "juwan";
        String password = "123qwe";

        // userService 모킹 하기
        // any(userName), any(password) 가 들어가면
        when(userService.join(any(), any()))
                .thenThrow(new RuntimeException("해당 userId가 중복 됩니다."));

        mockMvc.perform(post("/api/v1/users/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserJoinRequest(userName, password))))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    @WithMockUser
    void login_success() throws Exception {

        String userName = "juwan";
        String password = "123qwe";

        when(userService.login(any(), any()))
                .thenReturn("token");

        mockMvc.perform(post("/api/v1/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest(userName, password))))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("로그인 실패 테스트 - userName 없음")
    @WithMockUser
    void login_fail_case1() throws Exception {

        String userName = "juwan";
        String password = "123qwe";

        when(userService.login(any(), any()))
                .thenThrow(new AppException(ErrorCode.USERNAME_NOT_FOUND,""));

        mockMvc.perform(post("/api/v1/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest(userName, password))))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("로그인 실패 테스트 - password 틀림")
    @WithMockUser
    void login_fail_case2() throws Exception {


        String userName = "juwan";
        String password = "123qwe";

        when(userService.login(any(), any()))
                // any, any 를 씀 으로 안에 내용은 크게 중요하지 않다.
                .thenThrow(new AppException(ErrorCode.INVALID_PASSWORD,"잘못된 패스워드입니다."));

        mockMvc.perform(post("/api/v1/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest(userName, password))))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}