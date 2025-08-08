package com.lvluolang.game.service;

import com.lvluolang.game.entity.Review;
import com.lvluolang.game.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;
    
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
    
    public void likeReview(Long reviewId) {
        Optional<Review> reviewOpt = reviewRepository.findById(reviewId);
        if (reviewOpt.isPresent()) {
            Review review = reviewOpt.get();
            int currentLikes = review.getLikes() != null ? review.getLikes() : 0;
            review.setLikes(currentLikes + 1);
            reviewRepository.save(review);
        }
    }
}