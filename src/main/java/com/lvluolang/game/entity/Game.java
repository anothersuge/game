package com.lvluolang.game.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "games")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    
    /**
     * 游戏ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 游戏名称
     */
    @Column(nullable = false, unique = true)
    private String name;
    
    /**
     * 游戏描述
     */
    @Column(length = 1000)
    private String description;
    
    /**
     * 封面图片URL
     */
    @Column(nullable = false)
    private String coverImage;
    
    /**
     * 发布日期
     */
    @Column(nullable = false)
    private LocalDateTime releaseDate;
    
    /**
     * 开发商
     */
    @Column(nullable = false)
    private String developer;
    
    /**
     * 发行商
     */
    @Column(nullable = false)
    private String publisher;
    
    /**
     * 游戏类型
     */
    @Column(nullable = false)
    private String genre;
    
    /**
     * 平均评分
     */
    private Double averageRating;
    
    /**
     * 评分数量
     */
    private Integer ratingCount;
    
    /**
     * 创建时间
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    /**
     * 游戏的评价列表
     */
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews;
    
    /**
     * 实体创建前的预处理方法
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.averageRating = 0.0;
        this.ratingCount = 0;
    }
}