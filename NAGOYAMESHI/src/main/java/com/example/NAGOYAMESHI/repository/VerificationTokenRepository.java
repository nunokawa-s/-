package com.example.NAGOYAMESHI.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.NAGOYAMESHI.entity.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository< VerificationToken, Integer> {
	    public VerificationToken findByToken(String token);
}
