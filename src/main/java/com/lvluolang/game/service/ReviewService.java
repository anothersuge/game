package com.lvluolang.game.service;

import com.lvluolang.game.entity.Game;
import com.lvluolang.game.entity.Review;
import com.lvluolang.game.entity.ReviewLike;
import com.lvluolang.game.repository.ReviewRepository;
import com.lvluolang.game.repository.ReviewLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lvluolang.game.util.ClientIpUtil;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    
    private final ReviewRepository reviewRepository;
    
    private final ReviewLikeRepository reviewLikeRepository;
    
    private final GameService gameService;
    
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
    
    /**
     * Get recent reviews with game information to avoid N+1 query problem
     */
    public List<Review> getRecentReviewsWithGameByGameId(Long gameId) {
        return reviewRepository.findRecentReviewsWithGameByGameId(gameId);
    }
    
    public List<Review> getPopularReviewsByGameId(Long gameId) {
        return reviewRepository.findPopularReviewsByGameId(gameId);
    }
    
    @Transactional
    public Review saveReview(Review review) {
        Review savedReview = reviewRepository.save(review);
        gameService.updateGameRating(review.getGame().getId(), review.getRating());
        return savedReview;
    }
    
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }
    
    /**
     * Create and save a review for a game
     * @param gameName The name of the game
     * @param reviewerName The name of the reviewer
     * @param rating The rating given by the reviewer
     * @param reviewContent The content of the review
     * @return The saved review
     */
    public Review createReview(String gameName, String reviewerName, Double rating, String reviewContent) {
        // Get or create game using GameService
        Game game = gameService.getOrCreateGame(gameName);
        
        // Create and save review
        Review review = new Review();
        review.setUsername(reviewerName);
        review.setRating(rating);
        review.setContent(reviewContent);
        review.setGame(game);
        
        return saveReview(review);
    }
    
    public List<Review> getTopRatedReviews() {
        return reviewRepository.findTop10ByOrderByLikesDesc();
    }
    
    public List<Review> getRecentReviews() {
        return reviewRepository.findTop100WithGameByOrderByCreatedAtDesc();
    }
    
    /**
     * Like a review with IP address restriction
     * Each IP address can only like a review once
     */
    public boolean likeReview(Long reviewId) {
        // Get client IP address
        String clientIpAddress = ClientIpUtil.getClientIpAddress();
        
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
    

}