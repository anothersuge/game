package com.lvluolang.game.repository;

import com.lvluolang.game.entity.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    
    /**
     * Check if a review has been liked by an IP address
     */
    Optional<ReviewLike> findByReviewIdAndIpAddress(Long reviewId, String ipAddress);
}