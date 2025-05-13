package com.example.NAGOYAMESHI.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.NAGOYAMESHI.entity.Review;
import com.example.NAGOYAMESHI.entity.Shop;
import com.example.NAGOYAMESHI.entity.User;

public interface ReviewRepository extends JpaRepository<Review, Integer>{
	public List<Review> findTop6ByShopOrderByCreatedAtDesc(Shop shop);
	   public Review findByShopAndUser(Shop shop, User user);
	   public long countByShop(Shop shop);
	   public Page<Review> findByShopOrderByCreatedAtDesc(Shop shop, Pageable pageable);
  
}
