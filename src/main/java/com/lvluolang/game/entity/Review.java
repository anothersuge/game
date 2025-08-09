package com.lvluolang.game.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    
    /**
     * 评价ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 用户名
     */
    @Column(nullable = false)
    private String username;
    
    /**
     * 评分
     */
    @Column(nullable = false)
    private Double rating;
    
    /**
     * 评价内容
     */
    @Column(length = 5000)
    private String content;
    
    /**
     * 创建时间
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    /**
     * 关联的游戏
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;
    
    /**
     * 点赞数
     */
    private Integer likes;
    
    /**
     * 实体创建前的预处理方法
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.likes = 0;
        
        // 日志记录实体创建
        if (this.username != null && this.game != null && this.game.getName() != null) {
            System.out.println("正在创建评价实体: 用户=" + this.username + ", 游戏=" + this.game.getName());
        }
    }
}