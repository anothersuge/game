package com.lvluolang.game.repository;

import com.lvluolang.game.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    List<Review> findByGameId(Long gameId);
    
    List<Review> findByGameIdOrderByCreatedAtDesc(Long gameId);
    
    List<Review> findTop10ByOrderByLikesDesc();
    
    List<Review> findTop10ByOrderByCreatedAtDesc();
    
    List<Review> findTop100ByOrderByCreatedAtDesc();
    
    /**
     * Fetch top 100 reviews with game information using JOIN FETCH to avoid N+1 query problem
     */
    @Query("SELECT r FROM Review r JOIN FETCH r.game ORDER BY r.createdAt DESC")
    List<Review> findTop100WithGameByOrderByCreatedAtDesc();
    
    @Query("SELECT r FROM Review r WHERE r.game.id = :gameId ORDER BY r.createdAt DESC")
    List<Review> findRecentReviewsByGameId(@Param("gameId") Long gameId);
    
    @Query("SELECT r FROM Review r WHERE r.game.id = :gameId ORDER BY r.likes DESC")
    List<Review> findPopularReviewsByGameId(@Param("gameId") Long gameId);
    
    /**
     * Fetch reviews with game information using JOIN FETCH to avoid N+1 query problem
     */
    @Query("SELECT r FROM Review r JOIN FETCH r.game WHERE r.game.id = :gameId ORDER BY r.createdAt DESC")
    List<Review> findRecentReviewsWithGameByGameId(@Param("gameId") Long gameId);
}