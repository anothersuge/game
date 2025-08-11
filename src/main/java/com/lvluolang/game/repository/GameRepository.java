package com.lvluolang.game.repository;

import com.lvluolang.game.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    
    /**
     * 获取评分最高的10个游戏
     * @return 评分最高的游戏列表
     */
    List<Game> findTop10ByOrderByAverageRatingDesc();
    
    /**
     * 获取最近创建的10个游戏
     * @return 最近创建的游戏列表
     */
    List<Game> findTop10ByOrderByCreatedAtDesc();
    
    /**
     * 获取最近创建的100个游戏
     * @return 最近创建的游戏列表
     */
    List<Game> findTop100ByOrderByCreatedAtDesc();
    
    /**
     * 获取所有游戏，按创建时间倒序排列
     * @return 所有游戏列表，按创建时间倒序排列
     */
    List<Game> findAllByOrderByCreatedAtDesc();
    
    /**
     * 根据关键词搜索游戏
     * @param keyword 搜索关键词
     * @return 匹配的游戏列表
     */
    @Query("SELECT g FROM Game g WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(g.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Game> searchGames(@Param("keyword") String keyword);
    
    /**
     * 根据游戏类型查找游戏
     * @param genre 游戏类型
     * @return 匹配的游戏列表
     */
    List<Game> findByGenre(String genre);
    
    /**
     * 根据开发商查找游戏
     * @param developer 开发商
     * @return 匹配的游戏列表
     */
    List<Game> findByDeveloper(String developer);
     
    /**
     * 查找描述为"暂无描述"的游戏
     * @return 描述为"暂无描述"的游戏列表
     */
    @Query("SELECT g FROM Game g WHERE g.description = '暂无描述'")
    List<Game> findGamesWithDefaultDescription();
}