package com.lvluolang.game.repository;

import com.lvluolang.game.entity.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    
    /**
     * 检查评价是否已被指定IP地址点赞
     * @param reviewId 评价ID
     * @param ipAddress IP地址
     * @return 如果已点赞则返回ReviewLike对象，否则返回空
     */
    @Cacheable(value = "reviewLikes", key = "#reviewId + '_' + #ipAddress")
    Optional<ReviewLike> findByReviewIdAndIpAddress(Long reviewId, String ipAddress);
    
    /**
     * 删除指定时间之前的所有记录
     * @param dateTime 时间界限
     * @return 删除的记录数
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM ReviewLike r WHERE r.createdAt < :dateTime")
    int deleteByCreatedAtBefore(LocalDateTime dateTime);
}