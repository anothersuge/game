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
    
    /**
     * 获取所有游戏
     * @return 所有游戏列表
     */
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }
    
    /**
     * 根据ID获取游戏
     * @param id 游戏ID
     * @return 游戏对象，如果不存在则返回空
     */
    public Optional<Game> getGameById(Long id) {
        return gameRepository.findById(id);
    }
    
    /**
     * 保存游戏
     * @param game 游戏对象
     * @return 保存后的游戏对象
     */
    public Game saveGame(Game game) {
        return gameRepository.save(game);
    }
    
    /**
     * 删除游戏
     * @param id 游戏ID
     */
    public void deleteGame(Long id) {
        gameRepository.deleteById(id);
    }
    
    /**
     * 获取评分最高的游戏
     * @return 评分最高的游戏列表
     */
    public List<Game> getTopRatedGames() {
        return gameRepository.findTop10ByOrderByAverageRatingDesc();
    }
    
    /**
     * 获取最近创建的游戏
     * @return 最近创建的游戏列表
     */
    public List<Game> getRecentGames() {
        return gameRepository.findTop10ByOrderByCreatedAtDesc();
    }
    
    /**
     * 搜索游戏
     * @param keyword 搜索关键词
     * @return 匹配的游戏列表
     */
    public List<Game> searchGames(String keyword) {
        return gameRepository.searchGames(keyword);
    }
    
    /**
     * 根据游戏类型获取游戏
     * @param genre 游戏类型
     * @return 匹配的游戏列表
     */
    public List<Game> getGamesByGenre(String genre) {
        return gameRepository.findByGenre(genre);
    }
    
    /**
     * 根据开发商获取游戏
     * @param developer 开发商
     * @return 匹配的游戏列表
     */
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
     * 根据游戏名称获取游戏，如果不存在则创建新游戏
     * @param gameName 游戏名称
     * @return 已存在的或新创建的游戏
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
     * 异步生成游戏描述并更新游戏
     * @param gameName 游戏名称
     * @param gameId 要更新的游戏ID
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
     * 更新游戏描述
     * @param gameId 要更新的游戏ID
     * @param description 新的游戏描述
     */
    @Transactional
    public void updateGameDescription(Long gameId, String description) {
        gameRepository.findById(gameId).ifPresent(game -> {
            game.setDescription(description);
            gameRepository.save(game);
        });
    }
    
    /**
     * 更新游戏评分
     * @param gameId 游戏ID
     * @param newRating 新评分
     */
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