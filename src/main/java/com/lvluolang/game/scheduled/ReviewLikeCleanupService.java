package com.lvluolang.game.scheduled;

import com.lvluolang.game.repository.ReviewLikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewLikeCleanupService {

    private final ReviewLikeRepository reviewLikeRepository;
    private final CacheManager cacheManager;

    /**
     * 每小时执行一次，清理ReviewLike表中超过一天的记录
     * 首次延迟10分钟执行
     */
    @Scheduled(fixedRate = 3600000, initialDelay = 600000)
    public void cleanupExpiredReviewLikes() {
        log.info("开始执行ReviewLike过期记录清理任务");
        
        // 计算一天前的时间
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        
        // 删除超过一天的记录
        int deletedCount = reviewLikeRepository.deleteByCreatedAtBefore(oneDayAgo);
        
        // 清除reviewLikes缓存
        var cache = cacheManager.getCache("reviewLikes");
        if (cache != null) {
            cache.clear();
        }
        
        log.info("ReviewLike过期记录清理任务执行完成，共清理 {} 条记录", deletedCount);
    }
}