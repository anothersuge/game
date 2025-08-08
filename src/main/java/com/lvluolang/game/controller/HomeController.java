package com.lvluolang.game.controller;

import com.lvluolang.game.entity.Game;
import com.lvluolang.game.entity.Review;
import com.lvluolang.game.service.GameService;
import com.lvluolang.game.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {
    
    @Autowired
    private GameService gameService;
    
    @Autowired
    private ReviewService reviewService;
    
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
    
    @GetMapping("/game")
    public String gameDetail(@RequestParam Long id, Model model) {
        return gameService.getGameById(id)
                .map(game -> {
                    List<Review> reviews = reviewService.getRecentReviewsByGameId(id);
                    model.addAttribute("game", game);
                    model.addAttribute("reviews", reviews);
                    return "game-detail";
                })
                .orElse("redirect:/games");
    }
    
    @GetMapping("/search")
    public String search(@RequestParam String keyword, Model model) {
        List<Game> searchResults = gameService.searchGames(keyword);
        model.addAttribute("games", searchResults);
        model.addAttribute("keyword", keyword);
        return "search";
    }
}