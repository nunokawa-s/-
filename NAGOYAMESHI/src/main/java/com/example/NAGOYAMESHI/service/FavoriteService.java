package com.example.NAGOYAMESHI.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.NAGOYAMESHI.entity.Favorite;
import com.example.NAGOYAMESHI.entity.Shop;
import com.example.NAGOYAMESHI.entity.User;
import com.example.NAGOYAMESHI.repository.FavoriteRepository;

@Service
public class FavoriteService {
	private final FavoriteRepository favoriteRepository;

	public FavoriteService(FavoriteRepository favoriteRepository) {
		this.favoriteRepository = favoriteRepository;

	}

	@Transactional
	public void create(Shop shop, User user) {
		if (!isFavorite(shop, user)) {
			Favorite favorite = new Favorite();
			favorite.setShop(shop);
			favorite.setUser(user);
			favoriteRepository.save(favorite);
			System.out.println("物件ID: " + shop.getId() + "、ユーザーID: " + user.getId() + " をお気に入り登録しました。");
		} else {
			System.out.println("物件ID: " + shop.getId() + "、ユーザーID: " + user.getId() + " はすでにお気に入り登録されています。");
		}
	}

	public boolean isFavorite(Shop shop, User user) {
		return favoriteRepository.findByShopAndUser(shop, user) != null;
	}
}
