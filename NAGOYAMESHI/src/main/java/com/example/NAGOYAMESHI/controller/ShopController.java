package com.example.NAGOYAMESHI.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.NAGOYAMESHI.entity.Favorite;
import com.example.NAGOYAMESHI.entity.Review;
import com.example.NAGOYAMESHI.entity.Shop;
import com.example.NAGOYAMESHI.entity.User;
import com.example.NAGOYAMESHI.form.ReservationInputForm;
import com.example.NAGOYAMESHI.repository.FavoriteRepository;
import com.example.NAGOYAMESHI.repository.ReviewRepository;
import com.example.NAGOYAMESHI.repository.ShopRepository;
import com.example.NAGOYAMESHI.security.UserDetailsImpl;
import com.example.NAGOYAMESHI.service.FavoriteService;

@Controller
@RequestMapping("/shops")
public class ShopController {
	private final ShopRepository shopRepository;
	private final ReviewRepository reviewRepository;
	private final FavoriteRepository favoriteRepository;
	private final FavoriteService favoriteService;

	public ShopController(ShopRepository shopRepository, ReviewRepository reviewRepository,
			FavoriteRepository favoriteRepository, FavoriteService favoriteService) {
		this.shopRepository = shopRepository;
		this.reviewRepository = reviewRepository;
		this.favoriteRepository = favoriteRepository;
		this.favoriteService = favoriteService;
	}

	@GetMapping
	public String index(@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "area", required = false) String area,
			@RequestParam(name = "price", required = false) Integer price,
			@RequestParam(name = "order", required = false) String order,
			@RequestParam(name = "category", required = false) String category,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
			Model model) {

		Page<Shop> shopPage;

		if (category != null && !category.isEmpty()) {
			shopPage = shopRepository.findByCategories_Name(category, pageable);
		} else if (keyword != null && !keyword.isEmpty()) {
			if (order != null && order.equals("priceAsc")) {
				shopPage = shopRepository.findByNameLikeOrAddressLikeOrderByPriceAsc("%" + keyword + "%",
						"%" + keyword + "%", pageable);
			} else {
				shopPage = shopRepository.findByNameLikeOrAddressLikeOrderByCreatedAtDesc("%" + keyword + "%",
						"%" + keyword + "%", pageable);
			}
		} else if (area != null && !area.isEmpty()) {
			if (order != null && order.equals("priceAsc")) {
				shopPage = shopRepository.findByAddressLikeOrderByPriceAsc("%" + area + "%", pageable);
			} else {
				shopPage = shopRepository.findByAddressLikeOrderByCreatedAtDesc("%" + area + "%", pageable);
			}
		} else if (price != null) {
			if (order != null && order.equals("priceAsc")) {
				shopPage = shopRepository.findByPriceLessThanEqualOrderByPriceAsc(price, pageable);
			} else {
				shopPage = shopRepository.findByPriceLessThanEqualOrderByCreatedAtDesc(price, pageable);
			}
		} else {
			if (order != null && order.equals("priceAsc")) {
				shopPage = shopRepository.findAllByOrderByPriceAsc(pageable);
			} else {
				shopPage = shopRepository.findAllByOrderByCreatedAtDesc(pageable);
			}
		}

		model.addAttribute("shopPage", shopPage);
		model.addAttribute("keyword", keyword);
		model.addAttribute("area", area);
		model.addAttribute("price", price);
		model.addAttribute("order", order);
		model.addAttribute("category", category);

		return "shops/index";
	}

	@GetMapping("/{id}")
	public String show(@PathVariable(name = "id") Integer id,Model model,
			@PageableDefault(page = 0, size = 6, sort = "id") Pageable pageable, 
			 @ModelAttribute("reservationInputForm") ReservationInputForm reservationInputForm,
			@AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {

		Shop shop = shopRepository.getReferenceById(id);
		Favorite favorite = null;
		boolean hasUserAlreadyReviewed = false;
		boolean isFavorite = false;

		if (userDetailsImpl != null) {
			User user = userDetailsImpl.getUser();
			isFavorite = favoriteService.isFavorite(shop, user);
			if (isFavorite) {
				favorite = favoriteRepository.findByShopAndUser(shop, user);
			}
		}
		
		System.out.println("Monday Opening Hours: " + shop.getMondayOpeningHours());
		System.out.println("Monday Closing Hours: " + shop.getMondayClosingHours());
		System.out.println("Tuesday Opening Hours: " + shop.getTuesdayOpeningHours());
		System.out.println("Tuesday Closing Hours: " + shop.getTuesdayClosingHours());
		System.out.println("Wednesday Opening Hours: " + shop.getWednesdayOpeningHours());
		System.out.println("Wednesday Closing Hours: " + shop.getWednesdayClosingHours());
		System.out.println("Thursday Opening Hours: " + shop.getThursdayOpeningHours());
		System.out.println("Thursday Closing Hours: " + shop.getThursdayClosingHours());
		System.out.println("Friday Opening Hours: " + shop.getFridayOpeningHours());
		System.out.println("Friday Closing Hours: " + shop.getFridayClosingHours());
		System.out.println("Saturday Opening Hours: " + shop.getSaturdayOpeningHours());
		System.out.println("Saturday Closing Hours: " + shop.getSaturdayClosingHours());
		System.out.println("Sunday Opening Hours: " + shop.getSundayOpeningHours());
		System.out.println("Sunday Closing Hours: " + shop.getSundayClosingHours());

		Page<Review> reviewPage = reviewRepository.findByShopOrderByCreatedAtDesc(shop, pageable);

		List<String> condensedDaysWithHours = new ArrayList<>();
		addDayWithHours(condensedDaysWithHours, "月", shop.getMondayOpeningHours(), shop.getMondayClosingHours());
		addDayWithHours(condensedDaysWithHours, "火", shop.getTuesdayOpeningHours(), shop.getTuesdayClosingHours());
		addDayWithHours(condensedDaysWithHours, "水", shop.getWednesdayOpeningHours(), shop.getWednesdayClosingHours());
		addDayWithHours(condensedDaysWithHours, "木", shop.getThursdayOpeningHours(), shop.getThursdayClosingHours());
		addDayWithHours(condensedDaysWithHours, "金", shop.getFridayOpeningHours(), shop.getFridayClosingHours());
		addDayWithHours(condensedDaysWithHours, "土", shop.getSaturdayOpeningHours(), shop.getSaturdayClosingHours());
		addDayWithHours(condensedDaysWithHours, "日", shop.getSundayOpeningHours(), shop.getSundayClosingHours());

	    System.out.println("condensedDaysWithHours: " + condensedDaysWithHours); // ★ ログ出力 ★

		
		model.addAttribute("shop", shop);
		model.addAttribute("reservationInputForm", new ReservationInputForm());
		model.addAttribute("hasUserAlreadyReviewed", hasUserAlreadyReviewed);
		model.addAttribute("reviewPage", reviewPage);
		model.addAttribute("favorite", favorite);
		model.addAttribute("isFavorite", isFavorite);
		model.addAttribute("condensedDaysWithHours", condensedDaysWithHours); // ★ この行を追加 ★

		return "shops/show";
	}

	private void addDayWithHours(List<String> list, String day, String openingHours, String closingHours) {
	    if (openingHours != null && !openingHours.isEmpty() && closingHours != null && !closingHours.isEmpty()) {
	        // 時間のフォーマットを修正
	        String formattedOpeningHours = formatTime(openingHours);
	        String formattedClosingHours = formatTime(closingHours);
	        list.add(day + ":" + formattedOpeningHours + "〜" + formattedClosingHours);
	    }
	    // 開店時間または閉店時間のどちらか一方でも欠けている場合はリストに追加しない
	}

	private String formatTime(String time) {
	    if (time != null && time.length() == 4) {
	        return time.substring(0, 2) + ":" + time.substring(2, 4);
	    }
	    return time; // その他の形式の場合はそのまま返す
	}
}
