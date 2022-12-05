package com.example.springsecuritypractice.controller;

import com.example.springsecuritypractice.domian.dto.ReviewCreateReqest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {
    @PostMapping
    public String write(@RequestBody ReviewCreateReqest dto) {
        return "리뷰 등록에 성공했습니다.";
    }
}
