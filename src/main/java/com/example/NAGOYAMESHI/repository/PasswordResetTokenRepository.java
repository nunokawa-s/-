package com.example.NAGOYAMESHI.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.NAGOYAMESHI.entity.PasswordResetToken;
import com.example.NAGOYAMESHI.entity.User;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer>{
    public PasswordResetToken findByToken(String token);
    public PasswordResetToken findByUser(User user);

    void deleteByUser(User user);

}
