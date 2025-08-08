package com.lvluolang.game.service;

import com.lvluolang.game.entity.Review;
import com.lvluolang.game.entity.ReviewLike;
import com.lvluolang.game.repository.ReviewRepository;
import com.lvluolang.game.repository.ReviewLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private ReviewLikeRepository reviewLikeRepository;
    
    @Autowired
    private GameService gameService;
    
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }
    
    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }
    
    public List<Review> getReviewsByGameId(Long gameId) {
        return reviewRepository.findByGameId(gameId);
    }
    
    public List<Review> getRecentReviewsByGameId(Long gameId) {
        return reviewRepository.findRecentReviewsByGameId(gameId);
    }
    
    public List<Review> getPopularReviewsByGameId(Long gameId) {
        return reviewRepository.findPopularReviewsByGameId(gameId);
    }
    
    public Review saveReview(Review review) {
        Review savedReview = reviewRepository.save(review);
        gameService.updateGameRating(review.getGame().getId(), review.getRating());
        return savedReview;
    }
    
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }
    
    public List<Review> getTopRatedReviews() {
        return reviewRepository.findTop10ByOrderByLikesDesc();
    }
    
    public List<Review> getRecentReviews() {
        return reviewRepository.findTop10ByOrderByCreatedAtDesc();
    }
    
    /**
     * Like a review with IP address restriction
     * Each IP address can only like a review once
     */
    public boolean likeReview(Long reviewId) {
        // Get client IP address
        String clientIpAddress = getClientIpAddress();
        
        // Check if this IP has already liked this review
        Optional<ReviewLike> existingLike = reviewLikeRepository.findByReviewIdAndIpAddress(reviewId, clientIpAddress);
        
        // If already liked, return false
        if (existingLike.isPresent()) {
            return false;
        }
        
        // Get the review
        Optional<Review> reviewOpt = reviewRepository.findById(reviewId);
        if (reviewOpt.isPresent()) {
            Review review = reviewOpt.get();
            int currentLikes = review.getLikes() != null ? review.getLikes() : 0;
            review.setLikes(currentLikes + 1);
            reviewRepository.save(review);
            
            // Record this like
            ReviewLike reviewLike = new ReviewLike();
            reviewLike.setReviewId(reviewId);
            reviewLike.setIpAddress(clientIpAddress);
            reviewLikeRepository.save(reviewLike);
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Get client IP address from the request
     */
    private String getClientIpAddress() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return "unknown";
        }
        
        HttpServletRequest request = attributes.getRequest();
        String ipAddress = request.getHeader("X-Forwarded-For");
        
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("X-Real-IP");
        }
        
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        
        return ipAddress;
    }
}