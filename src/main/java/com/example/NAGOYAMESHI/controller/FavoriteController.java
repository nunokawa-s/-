package com.example.NAGOYAMESHI.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.NAGOYAMESHI.entity.Favorite;
import com.example.NAGOYAMESHI.entity.Shop;
import com.example.NAGOYAMESHI.entity.User;
import com.example.NAGOYAMESHI.repository.FavoriteRepository;
import com.example.NAGOYAMESHI.repository.ShopRepository;
import com.example.NAGOYAMESHI.security.UserDetailsImpl;

@Controller
public class FavoriteController {
	private final FavoriteRepository favoriteRepository;
	private final ShopRepository shopRepository;

	public FavoriteController(FavoriteRepository favoriteRepository, ShopRepository shopRepository) {
		this.favoriteRepository = favoriteRepository;
		this.shopRepository = shopRepository;
	}

	@GetMapping("/favorites")
	public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			@PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable, Model model) {
		User user = userDetailsImpl.getUser();
		Page<Favorite> favoritePage = favoriteRepository.findByUserOrderByCreatedAtDesc(user, pageable);

		model.addAttribute("favoritePage", favoritePage);

		return "favorites/index";
	}

	@PostMapping("/shops/{shopId}/favorites/create")
	public String create(@PathVariable(name = "shopId") Integer shopId,
			@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			RedirectAttributes redirectAttributes,
			Model model) {

		Shop shop = shopRepository.getReferenceById(shopId);
		User user = userDetailsImpl.getUser();
		Favorite favorite = favoriteRepository.findByShopAndUser(shop, user);

		if (favorite == null) {
			// まだ登録されていないので登録処理
			Favorite newFavorite = new Favorite();
			newFavorite.setShop(shop);
			newFavorite.setUser(user);
			favoriteRepository.save(newFavorite);
			redirectAttributes.addFlashAttribute("successMessage", "お気に入りに追加しました。");
		} else {
			// すでに登録済み
			redirectAttributes.addFlashAttribute("errorMessage", "すでにお気に入りに追加済みです。");
		}

		return "redirect:/shops/{shopId}";
	}

	@PostMapping("/shops/{shopId}/favorites/{favoriteId}/delete")
	public String delete(@PathVariable(name = "favoriteId") Integer favoriteId, RedirectAttributes redirectAttributes) {
		if (favoriteRepository.existsById(favoriteId)) {

			favoriteRepository.deleteById(favoriteId);

			redirectAttributes.addFlashAttribute("successMessage", "お気に入りを解除しました。");
		} else {
			redirectAttributes.addFlashAttribute("errorMessage", "指定されたお気に入りは存在しませんでした。");
		}
		return "redirect:/shops/{shopId}";
	}
}
