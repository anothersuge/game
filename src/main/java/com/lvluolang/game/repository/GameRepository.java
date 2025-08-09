package com.lvluolang.game.repository;

import com.lvluolang.game.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    
    List<Game> findTop10ByOrderByAverageRatingDesc();
    
    List<Game> findTop10ByOrderByCreatedAtDesc();
    
    @Query("SELECT g FROM Game g WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(g.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Game> searchGames(@Param("keyword") String keyword);
    
    List<Game> findByGenre(String genre);
    
    List<Game> findByDeveloper(String developer);
    
    @Query("SELECT g FROM Game g WHERE g.description = '暂无描述' OR g.description = '暂无介绍'")
    List<Game> findGamesWithDefaultDescription();
}