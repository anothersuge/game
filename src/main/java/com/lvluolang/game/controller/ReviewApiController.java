package com.lvluolang.game.controller;

import com.lvluolang.game.entity.Review;
import com.lvluolang.game.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    
    /**
     * 根据游戏ID获取评论
     * @param gameId 游戏ID
     * @return 评论列表
     */
    @GetMapping("/game/{gameId}")
    public List<Review> getReviewsByGameId(@PathVariable Long gameId) {
        return reviewService.getRecentReviewsWithGameByGameId(gameId);
    }
}