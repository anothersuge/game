package com.lvluolang.game.controller;

import com.lvluolang.game.entity.Game;
import com.lvluolang.game.annotation.SensitiveWordCheck;
import com.lvluolang.game.entity.Review;
import com.lvluolang.game.service.GameService;
import com.lvluolang.game.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {
    
    private final GameService gameService;
    
    private final ReviewService reviewService;
    
    /**
     * 提交游戏评价
     * @param requestBody 包含游戏评价信息的请求体
     * @return 提交结果，成功返回"success"
     */
    @PostMapping("/submitReview")
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
     * 显示首页
     * @param model 视图模型
     * @return 首页视图名称
     */
    @GetMapping("/")
    public String home(Model model) {
        
        List<Game> topRatedGames = gameService.getTopRatedGames();
        List<Game> recentGames = gameService.getRecentGames();
        List<Review> recentReviews = reviewService.getRecentReviews();
        
        model.addAttribute("topRatedGames", topRatedGames);
        model.addAttribute("recentGames", recentGames);
        model.addAttribute("recentReviews", recentReviews);
        
        return "index";
    }
    
    /**
     * 显示所有游戏列表
     * @param model 视图模型
     * @return 游戏列表页视图名称
     */
    @GetMapping("/games")
    public String games(Model model) {
        
        List<Game> allGames = gameService.getAllGamesOrderByCreatedAtDesc();
        model.addAttribute("games", allGames);
        return "games";
    }
    
    /**
     * 显示游戏详情
     * @param id 游戏ID
     * @param model 视图模型
     * @return 游戏详情页视图名称，如果游戏不存在则重定向到首页
     */
    @GetMapping("/game/{id}")
    public String gameDetail(@PathVariable Long id, Model model) {
        
        return gameService.getGameById(id)
                .map(game -> {
                    List<Review> reviews = reviewService.getRecentReviewsWithGameByGameId(id);
                    model.addAttribute("game", game);
                    model.addAttribute("reviews", reviews);
                    
                    return "game-detail";
                })
                .orElse("redirect:/");
    }
    
    /**
     * 搜索游戏
     * @param keyword 搜索关键词
     * @param model 视图模型
     * @return 搜索结果页视图名称
     */
    @GetMapping("/search")
    public String search(@RequestParam String keyword, Model model) {
        
        List<Game> searchResults = gameService.searchGames(keyword);
        model.addAttribute("games", searchResults);
        model.addAttribute("keyword", keyword);
    
        return "search";
    }
    
    /**
     * 为评价点赞
     * @param reviewId 评价ID
     * @return 点赞结果，成功返回"success"，已点赞返回"already_liked"
     */
    @PostMapping("/likeReview")
    @ResponseBody
    public String likeReview(@RequestParam Long reviewId) {
        boolean success = reviewService.likeReview(reviewId);
        if (success) {
            log.info("点赞成功，评价ID: {}", reviewId);
        } else {
            log.info("点赞失败，评价已点赞，评价ID: {}", reviewId);
        }
        
        return success ? "success" : "already_liked";
    }
}