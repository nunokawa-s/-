package com.example.NAGOYAMESHI.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.NAGOYAMESHI.entity.Review;
import com.example.NAGOYAMESHI.entity.Shop;
import com.example.NAGOYAMESHI.entity.User;
import com.example.NAGOYAMESHI.form.ReviewEditForm;
import com.example.NAGOYAMESHI.form.ReviewPostForm;
import com.example.NAGOYAMESHI.repository.ReviewRepository;
import com.example.NAGOYAMESHI.repository.ShopRepository;
import com.example.NAGOYAMESHI.repository.UserRepository;
@Service
public class ReviewService {
	private final ReviewRepository reviewRepository;

	public ReviewService(ReviewRepository reviewRepository, ShopRepository shopRepository,
			UserRepository userRepository) {
		this.reviewRepository = reviewRepository;
	}

	@Transactional
	public void create(Shop shop, User user, ReviewPostForm reviewPostForm) {
		Review review = new Review();

		review.setShop(shop);
		review.setUser(user);
		review.setReviewScore(reviewPostForm.getReviewScore());
		review.setReviewText(reviewPostForm.getReviewText());

		reviewRepository.save(review);
	}

	public void update(ReviewEditForm reviewEditForm) {
		Review review = reviewRepository.getReferenceById(reviewEditForm.getId());

		review.setReviewScore(reviewEditForm.getReviewScore());
		review.setReviewText(reviewEditForm.getReviewText());

		reviewRepository.save(review);
	}
	  public boolean hasUserAlreadyReviewed(Shop shop, User user) {
	       return reviewRepository.findByShopAndUser(shop, user) != null;
}
}
