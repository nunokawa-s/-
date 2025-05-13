package com.example.NAGOYAMESHI.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.NAGOYAMESHI.entity.Favorite;
import com.example.NAGOYAMESHI.entity.Shop;
import com.example.NAGOYAMESHI.entity.User;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
public Page<Favorite> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
public Favorite findByShopAndUser(Shop shop, User user);
}
