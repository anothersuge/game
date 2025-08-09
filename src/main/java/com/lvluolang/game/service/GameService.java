package com.lvluolang.game.service;

import com.lvluolang.game.entity.Game;
import com.lvluolang.game.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GameService {
    
    @Autowired
    private GameRepository gameRepository;
    
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }
    
    public Optional<Game> getGameById(Long id) {
        return gameRepository.findById(id);
    }
    
    public Game saveGame(Game game) {
        return gameRepository.save(game);
    }
    
    public void deleteGame(Long id) {
        gameRepository.deleteById(id);
    }
    
    public List<Game> getTopRatedGames() {
        return gameRepository.findTop10ByOrderByAverageRatingDesc();
    }
    
    public List<Game> getRecentGames() {
        return gameRepository.findTop10ByOrderByCreatedAtDesc();
    }
    
    public List<Game> searchGames(String keyword) {
        return gameRepository.searchGames(keyword);
    }
    
    public List<Game> getGamesByGenre(String genre) {
        return gameRepository.findByGenre(genre);
    }
    
    public List<Game> getGamesByDeveloper(String developer) {
        return gameRepository.findByDeveloper(developer);
    }
    
    @Transactional
    public void updateGameRating(Long gameId, Double newRating) {
        Optional<Game> gameOpt = gameRepository.findById(gameId);
        if (gameOpt.isPresent()) {
            Game game = gameOpt.get();
            int currentCount = game.getRatingCount() != null ? game.getRatingCount() : 0;
            double currentAvg = game.getAverageRating() != null ? game.getAverageRating() : 0.0;
            
            double totalRating = currentAvg * currentCount + newRating;
            game.setAverageRating(totalRating / (currentCount + 1));
            game.setRatingCount(currentCount + 1);
            
            gameRepository.save(game);
        }
    }
}