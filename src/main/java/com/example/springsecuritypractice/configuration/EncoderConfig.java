package com.example.springsecuritypractice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class EncoderConfig {
    // EncoderConfig 이유 DB 정보를 봐도 encoder 을 해서 2중 으로 보안을 한다

    // BCryptPasswordEncoder 와 SecurityFilterChain 는 꼭 다른 클래스에 넣어줘야한다
    // 나중에 순환참조에서 오류가 생길수도 있다고 한다.
    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
