package com.lvluolang.game.controller;

import com.lvluolang.game.entity.Game;
import com.lvluolang.game.entity.Review;
import com.lvluolang.game.annotation.SensitiveWordCheck;
import com.lvluolang.game.service.GameService;
import com.lvluolang.game.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
@Slf4j
public class GameApiController {
    
    private final GameService gameService;
    private final ReviewService reviewService;
    
    /**
     * 获取首页数据
     * @return 首页数据
     */
    @GetMapping("/home")
    public Map<String, Object> home() {
        List<Game> topRatedGames = gameService.getTopRatedGames();
        List<Game> recentGames = gameService.getRecentGames();
        List<Review> recentReviews = reviewService.getRecentReviews();
        
        return Map.of(
            "topRatedGames", topRatedGames,
            "recentGames", recentGames,
            "recentReviews", recentReviews
        );
    }
    
    /**
     * 获取所有游戏列表
     * @return 游戏列表
     */
    @GetMapping
    public List<Game> games() {
        return gameService.getRecent100Games();
    }
    
    /**
     * 获取游戏详情
     * @param id 游戏ID
     * @return 游戏详情数据
     */
    @GetMapping("/{id}")
    public Map<String, Object> gameDetail(@PathVariable Long id) {
        return gameService.getGameById(id)
                .map(game -> {
                    List<Review> reviews = reviewService.getRecentReviewsWithGameByGameId(id);
                    return Map.of(
                        "game", game,
                        "reviews", reviews
                    );
                })
                .orElseThrow(() -> new RuntimeException("Game not found"));
    }
    
    /**
     * 搜索游戏
     * @param keyword 搜索关键词
     * @return 搜索结果
     */
    @GetMapping("/search")
    public Map<String, Object> search(@RequestParam String keyword) {
        List<Game> searchResults = gameService.searchGames(keyword);
        return Map.of(
            "games", searchResults,
            "keyword", keyword
        );
    }
    
    /**
     * 提交游戏评价
     * @param requestBody 包含游戏评价信息的请求体
     * @return 提交结果，成功返回"success"
     */
    @PostMapping("/review")
    @ResponseBody
    @SensitiveWordCheck
    public String submitReview(@RequestBody Map<String, Object> requestBody) {
        // Extract data from JSON request body
        String gameName = (String) requestBody.get("gameName");
        String reviewerName = (String) requestBody.get("reviewerName");
        Double rating = ((Number) requestBody.get("rating")).doubleValue();
        String reviewContent = (String) requestBody.get("reviewContent");
        
        // Create and save review using ReviewService
        reviewService.createReview(gameName, reviewerName, rating, reviewContent);
        
        log.info("评价提交成功: 游戏={}, 评价者={}, 评分={}", gameName, reviewerName, rating);
        
        return "success";
    }
    
    /**
     * 为评价点赞
     * @param reviewId 评价ID
     * @return 点赞结果，成功返回"success"，已点赞返回"already_liked"
     */
    @PostMapping("/review/{reviewId}/like")
    @ResponseBody
    public String likeReview(@PathVariable Long reviewId) {
        boolean success = reviewService.likeReview(reviewId);
        if (success) {
            log.info("点赞成功，评价ID: {}", reviewId);
        } else {
            log.info("点赞失败，评价已点赞，评价ID: {}", reviewId);
        }
        
        return success ? "success" : "already_liked";
    }
}