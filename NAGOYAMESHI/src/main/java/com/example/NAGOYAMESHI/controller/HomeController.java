package com.example.NAGOYAMESHI.controller;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.NAGOYAMESHI.entity.Shop;
import com.example.NAGOYAMESHI.repository.ShopRepository;


@Controller
public class HomeController {
	private final ShopRepository shopRepository;
	
	public HomeController(ShopRepository shopRepository) {
		this.shopRepository = shopRepository;
	}
	 @GetMapping("/")
	    public String index(Model model) {
	       List<Shop> newShop = shopRepository.findTop10ByOrderByCreatedAtDesc();

	       
	       model.addAttribute("newShops", newShop);
		 
		 
		 return "index";
	        
		}
	}