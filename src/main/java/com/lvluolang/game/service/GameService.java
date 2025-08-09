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
    
    /**
     * Get a game by name, or create a new one if it doesn't exist
     * @param gameName The name of the game
     * @return The existing or newly created game
     */
    public Game getOrCreateGame(String gameName) {
        // Check if game exists
        Optional<Game> existingGame = getAllGames().stream()
                .filter(game -> game.getName().equals(gameName))
                .findFirst();
        
        if (existingGame.isPresent()) {
            return existingGame.get();
        } else {
            // Create new game
            Game game = new Game();
            game.setName(gameName);
            game.setDescription("暂无描述");
            game.setCoverImage("/images/default-cover.jpg");
            game.setDeveloper("未知");
            game.setPublisher("未知");
            game.setGenre("未知");
            game.setReleaseDate(java.time.LocalDateTime.now());
            return saveGame(game);
        }
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