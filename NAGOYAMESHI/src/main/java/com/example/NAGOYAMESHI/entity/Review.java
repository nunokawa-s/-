package com.example.NAGOYAMESHI.entity;


import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Table(name = "reviews")
@Data
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop; // Shopエンティティとの関連付け

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // Userエンティティとの関連付け

    @Column(name = "review_score")
    private Integer reviewScore;

    @Column(name = "review_text")
    private String reviewText;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;


}