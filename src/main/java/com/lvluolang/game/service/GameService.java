package com.lvluolang.game.service;

import com.lvluolang.game.entity.Game;
import com.lvluolang.game.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GameService {
    
    private final GameRepository gameRepository;
    private final AiService aiService;
    
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
     * 获取描述为"暂无描述"的游戏
     * @return 描述为"暂无描述"的游戏列表
     */
    public List<Game> getGamesDefaultDescription() {
        return gameRepository.findGamesWithDefaultDescription();
    }
    
    /**
     * Get a game by name, or create a new one if it doesn't exist
     * @param gameName The name of the game
     * @return The existing or newly created game
     */
    public Game getOrCreateGame(String gameName) {
        return getAllGames().stream()
                .filter(game -> game.getName().equals(gameName))
                .findFirst()
                .orElseGet(() -> {
                    // Create new game
                    Game game = new Game();
                    game.setName(gameName);
                    
                    // Set default description initially
                    game.setDescription("暂无描述");
                    
                    game.setCoverImage("/images/default-cover.jpg");
                    game.setDeveloper("未知");
                    game.setPublisher("未知");
                    game.setGenre("未知");
                    game.setReleaseDate(java.time.LocalDateTime.now());
                    
                    // Save the game first
                    Game savedGame = saveGame(game);
                    
                    // Generate game description asynchronously
                    generateGameDescriptionAsync(gameName, savedGame.getId());
                    
                    return savedGame;
                });
    }
    
    /**
     * Asynchronously generate game description and update the game
     * @param gameName The name of the game
     * @param gameId The ID of the game to update
     */
    public void generateGameDescriptionAsync(String gameName, Long gameId) {
        CompletableFuture.runAsync(() -> {
            try {
                String description = aiService.generateGameDescription(gameName);
                updateGameDescription(gameId, description);
            } catch (Exception e) {
                // Log the error, but don't change the default description
                System.err.println("Failed to generate AI description for game: " + gameName + ", error: " + e.getMessage());
            }
        });
    }
    
    /**
     * Update game description
     * @param gameId The ID of the game to update
     * @param description The new description
     */
    @Transactional
    public void updateGameDescription(Long gameId, String description) {
        gameRepository.findById(gameId).ifPresent(game -> {
            game.setDescription(description);
            gameRepository.save(game);
        });
    }
    
    @Transactional
    public void updateGameRating(Long gameId, Double newRating) {
        gameRepository.findById(gameId).ifPresent(game -> {
            int currentCount = game.getRatingCount() != null ? game.getRatingCount() : 0;
            double currentAvg = game.getAverageRating() != null ? game.getAverageRating() : 0.0;
            
            double totalRating = currentAvg * currentCount + newRating;
            game.setAverageRating(totalRating / (currentCount + 1));
            game.setRatingCount(currentCount + 1);
            
            gameRepository.save(game);
        });
    }
}