package com.example.springsecuritypractice.service;

import ch.qos.logback.core.encoder.EchoEncoder;
import com.example.springsecuritypractice.domian.User;
import com.example.springsecuritypractice.exception.AppException;
import com.example.springsecuritypractice.exception.ErrorCode;
import com.example.springsecuritypractice.repository.UserRepository;
import com.example.springsecuritypractice.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor  // 생성자 생성
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.token.secret}")
    private String key;
    private Long expireTimeMs = 1000 * 60 * 60L;

    public String join(String userName, String password) {
        // userName 중복 check
        userRepository.findByUserName(userName)
                // 만약 있으면 ifPresent
                .ifPresent(user -> {
                    throw new RuntimeException(userName+"은 이미 존재하는 회원입니다.");
//                    throw new AppException(ErrorCode.USERNAME_DUPLICATED, userName+"은 이미 존재하는 회원입니다.");
                });

        // 저장
        User user = User.builder()
                .userName(userName)
                .password(encoder.encode(password))
                .build();
        userRepository.save(user);

        return "SUCCESS";
    }


    public String login(String userName, String password) {

        // userName 없음
        User selectedUser = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, userName + " 이 없습니다."));


        // password 틀림
        if (!encoder.matches(password,selectedUser.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD, "패스워드를 잘못 입력했습니다.");
        }


        // 성공
        String token = JwtTokenUtil.createToken(selectedUser.getUserName(), key, expireTimeMs);


        return token;
    }
}
