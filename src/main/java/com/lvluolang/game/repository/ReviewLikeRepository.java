package com.lvluolang.game.repository;

import com.lvluolang.game.entity.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    
    /**
     * 检查评价是否已被指定IP地址点赞
     * @param reviewId 评价ID
     * @param ipAddress IP地址
     * @return 如果已点赞则返回ReviewLike对象，否则返回空
     */
    Optional<ReviewLike> findByReviewIdAndIpAddress(Long reviewId, String ipAddress);
}