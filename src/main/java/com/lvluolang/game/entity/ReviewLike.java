package com.lvluolang.game.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "review_likes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"review_id", "ip_address"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewLike {
    
    /**
     * 点赞ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 关联的评价ID
     */
    @Column(name = "review_id", nullable = false)
    private Long reviewId;
    
    /**
     * IP地址
     */
    @Column(name = "ip_address", nullable = false)
    private String ipAddress;
    
    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    /**
     * 实体创建前的预处理方法
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        
        // 日志记录实体创建
        System.out.println("正在创建点赞实体: 评价ID=" + this.reviewId + ", IP=" + this.ipAddress);
    }
}