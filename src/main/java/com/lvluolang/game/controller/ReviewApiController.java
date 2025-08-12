package com.lvluolang.game.controller;

import com.lvluolang.game.entity.Review;
import com.lvluolang.game.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewApiController {
    
    private final ReviewService reviewService;
    
    /**
     * 获取最近的评论
     * @return 最近的评论列表
     */
    @GetMapping("/recent")
    public List<Review> getRecentReviews() {
        return reviewService.getRecentReviews();
    }
}