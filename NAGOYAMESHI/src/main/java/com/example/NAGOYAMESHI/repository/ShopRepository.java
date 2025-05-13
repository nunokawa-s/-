package com.example.NAGOYAMESHI.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.NAGOYAMESHI.entity.Shop;

public interface ShopRepository  extends JpaRepository<Shop, Integer>{

    public Page<Shop> findByNameLike(String keyword, Pageable pageable);

    public Page<Shop> findByNameLikeOrAddressLikeOrderByCreatedAtDesc(String nameKeyword, String addressKeyword,
            Pageable pageable);

    public Page<Shop> findByNameLikeOrAddressLikeOrderByPriceAsc(String nameKeyword, String addressKeyword,
            Pageable pageable);

    public Page<Shop> findByAddressLikeOrderByCreatedAtDesc(String area, Pageable pageable);

    public Page<Shop> findByAddressLikeOrderByPriceAsc(String area, Pageable pageable);
    
    public Page<Shop> findByAddressLikeOrderByPriceDesc(String area, Pageable pageable);

    public Page<Shop> findByPriceLessThanEqualOrderByCreatedAtDesc(Integer price, Pageable pageable);

    public Page<Shop> findByPriceLessThanEqualOrderByPriceAsc(Integer price, Pageable pageable);

    public Page<Shop> findAllByOrderByCreatedAtDesc(Pageable pageable);

    public Page<Shop> findAllByOrderByPriceAsc(Pageable pageable);
	
	List<Shop> findTop10ByOrderByCreatedAtDesc();
	
	 Page<Shop> findByCategories_Name(String categoryName, Pageable pageable);

}