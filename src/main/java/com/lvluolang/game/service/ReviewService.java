package com.lvluolang.game.service;

import com.lvluolang.game.entity.Game;
import com.lvluolang.game.entity.Review;
import com.lvluolang.game.entity.ReviewLike;
import com.lvluolang.game.repository.ReviewRepository;
import com.lvluolang.game.repository.ReviewLikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lvluolang.game.util.ClientIpUtil;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {
    
    private final ReviewRepository reviewRepository;
    
    private final ReviewLikeRepository reviewLikeRepository;
    
    private final GameService gameService;
    
    /**
     * 获取所有评论
     * @return 所有评论列表
     */
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }
    
    /**
     * 根据ID获取评论
     * @param id 评论ID
     * @return 评论对象，如果不存在则返回空
     */
    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }
    
    /**
     * 根据游戏ID获取所有评论
     * @param gameId 游戏ID
     * @return 该游戏的所有评论
     */
    public List<Review> getReviewsByGameId(Long gameId) {
        return reviewRepository.findByGameId(gameId);
    }
    
    /**
     * 获取指定游戏的最近评论
     * @param gameId 游戏ID
     * @return 指定游戏的最近评论列表
     */
    public List<Review> getRecentReviewsByGameId(Long gameId) {
        return reviewRepository.findRecentReviewsByGameId(gameId);
    }
    
    /**
     * 获取指定游戏的最近评论及游戏信息，避免N+1查询问题
     * @param gameId 游戏ID
     * @return 包含游戏信息的最近评论列表
     */
    public List<Review> getRecentReviewsWithGameByGameId(Long gameId) {
        return reviewRepository.findRecentReviewsWithGameByGameId(gameId);
    }
    
    /**
     * 获取指定游戏的热门评论
     * @param gameId 游戏ID
     * @return 指定游戏的热门评论列表
     */
    public List<Review> getPopularReviewsByGameId(Long gameId) {
        return reviewRepository.findPopularReviewsByGameId(gameId);
    }
    
    /**
     * 保存评论并更新游戏评分
     * @param review 评论对象
     * @return 保存后的评论对象
     */
    @Transactional
    public Review saveReview(Review review) {
        log.info("正在保存评论，游戏ID: {}, 评论者: {}", review.getGame().getId(), review.getUsername());
        Review savedReview = reviewRepository.save(review);
        log.info("评论保存成功，ID: {}", savedReview.getId());
        gameService.updateGameRating(review.getGame().getId(), review.getRating());
        return savedReview;
    }
    
    /**
     * 删除评论
     * @param id 评论ID
     */
    public void deleteReview(Long id) {
        log.info("正在删除评论，ID: {}", id);
        reviewRepository.deleteById(id);
        log.info("评论删除成功，ID: {}", id);
    }
    
    /**
     * 为游戏创建并保存评论
     * @param gameName 游戏名称
     * @param reviewerName 评论者名称
     * @param rating 评分
     * @param reviewContent 评论内容
     * @return 保存后的评论
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
    
    /**
     * 获取评分最高的评论
     * @return 评分最高的评论列表
     */
    public List<Review> getTopRatedReviews() {
        return reviewRepository.findTop10ByOrderByLikesDesc();
    }
    
    /**
     * 获取最近的评论
     * @return 最近的评论列表
     */
    public List<Review> getRecentReviews() {
        return reviewRepository.findTop100WithGameByOrderByCreatedAtDesc();
    }
    
    /**
     * 点赞评论，使用IP地址限制，每个IP地址只能点赞一次
     * @param reviewId 评论ID
     * @return 是否点赞成功
     */
    public boolean likeReview(Long reviewId) {
        // Get client IP address
        String clientIpAddress = ClientIpUtil.getClientIpAddress();
        log.info("正在处理评论点赞，评论ID: {}, IP地址: {}", reviewId, clientIpAddress);
        
        // Check if this IP has already liked this review
        Optional<ReviewLike> existingLike = reviewLikeRepository.findByReviewIdAndIpAddress(reviewId, clientIpAddress);
        
        // If already liked, return false
        if (existingLike.isPresent()) {
            log.info("该IP地址已为评论点赞，评论ID: {}, IP地址: {}", reviewId, clientIpAddress);
            return false;
        }
        
        // Get the review and update likes
        return reviewRepository.findById(reviewId)
                .map(review -> {
                    int currentLikes = review.getLikes() != null ? review.getLikes() : 0;
                    review.setLikes(currentLikes + 1);
                    reviewRepository.save(review);
                    
                    // Record this like
                    ReviewLike reviewLike = new ReviewLike();
                    reviewLike.setReviewId(reviewId);
                    reviewLike.setIpAddress(clientIpAddress);
                    reviewLikeRepository.save(reviewLike);
                    
                    log.info("评论点赞成功，评论ID: {}, 新点赞数: {}, IP地址: {}", reviewId, review.getLikes(), clientIpAddress);
                    return true;
                })
                .orElse(false);
    }
    

}