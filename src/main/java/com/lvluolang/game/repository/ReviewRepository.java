package com.lvluolang.game.repository;

import com.lvluolang.game.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    /**
     * 根据游戏ID查找评价
     * @param gameId 游戏ID
     * @return 匹配的评价列表
     */
    List<Review> findByGameId(Long gameId);
    
    /**
     * 根据游戏ID查找评价，按创建时间倒序排列
     * @param gameId 游戏ID
     * @return 匹配的评价列表
     */
    List<Review> findByGameIdOrderByCreatedAtDesc(Long gameId);
    
    /**
     * 获取点赞数最高的10个评价
     * @return 点赞数最高的评价列表
     */
    List<Review> findTop10ByOrderByLikesDesc();
    
    /**
     * 获取最近创建的100个评价
     * @return 最近创建的评价列表
     */
    List<Review> findTop100ByOrderByCreatedAtDesc();
    
    /**
     * 获取最近创建的100个评价及其关联游戏信息，使用JOIN FETCH避免N+1查询问题
     * @return 评价列表
     */
    @Query("SELECT r FROM Review r JOIN FETCH r.game ORDER BY r.createdAt DESC")
    List<Review> findTop100WithGameByOrderByCreatedAtDesc();
    
    /**
     * 根据游戏ID查找最近创建的评价
     * @param gameId 游戏ID
     * @return 匹配的评价列表
     */
    @Query("SELECT r FROM Review r WHERE r.game.id = :gameId ORDER BY r.createdAt DESC")
    List<Review> findRecentReviewsByGameId(@Param("gameId") Long gameId);
    
    /**
     * 根据游戏ID查找最受欢迎的评价（按点赞数排序）
     * @param gameId 游戏ID
     * @return 匹配的评价列表
     */
    @Query("SELECT r FROM Review r WHERE r.game.id = :gameId ORDER BY r.likes DESC")
    List<Review> findPopularReviewsByGameId(@Param("gameId") Long gameId);
    
    /**
     * 获取指定游戏的最近评价及其关联游戏信息，使用JOIN FETCH避免N+1查询问题
     * @param gameId 游戏ID
     * @return 评价列表
     */
    @Query("SELECT r FROM Review r JOIN FETCH r.game WHERE r.game.id = :gameId ORDER BY r.createdAt DESC")
    List<Review> findRecentReviewsWithGameByGameId(@Param("gameId") Long gameId);
}