package com.lvluolang.game.controller;

import com.lvluolang.game.entity.Game;
import com.lvluolang.game.annotation.SensitiveWordCheck;
import com.lvluolang.game.entity.Review;
import com.lvluolang.game.service.GameService;
import com.lvluolang.game.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    
    @Autowired
    private GameService gameService;
    
    @Autowired
    private ReviewService reviewService;
    
    
    
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
        reviewService.createReview(gameName, reviewerName, rating, reviewContent, gameService);
        
        return "success";
    }
    
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
    
    @GetMapping("/games")
    public String games(Model model) {
        List<Game> allGames = gameService.getAllGames();
        model.addAttribute("games", allGames);
        return "games";
    }
    
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
    
    @GetMapping("/search")
    public String search(@RequestParam String keyword, Model model) {
        List<Game> searchResults = gameService.searchGames(keyword);
        model.addAttribute("games", searchResults);
        model.addAttribute("keyword", keyword);
        return "search";
    }
    
    @PostMapping("/likeReview")
    @ResponseBody
    public String likeReview(@RequestParam Long reviewId) {
        boolean success = reviewService.likeReview(reviewId);
        return success ? "success" : "already_liked";
    }
}