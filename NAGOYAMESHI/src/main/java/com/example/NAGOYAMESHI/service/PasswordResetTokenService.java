package com.example.NAGOYAMESHI.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.NAGOYAMESHI.entity.PasswordResetToken;
import com.example.NAGOYAMESHI.entity.User;
import com.example.NAGOYAMESHI.repository.PasswordResetTokenRepository;
import com.example.NAGOYAMESHI.repository.UserRepository;

@Service
public class PasswordResetTokenService {
	private final PasswordResetTokenRepository passwordResetTokenRepository;
	private final UserRepository userRepository;

	public PasswordResetTokenService(PasswordResetTokenRepository passwordResetTokenRepository,
			UserRepository userRepository) {
		this.passwordResetTokenRepository = passwordResetTokenRepository;
		this.userRepository = userRepository;
	}

	   public User findUserByEmail(String email) {
	        return userRepository.findByEmail(email);
	    }

	@Transactional
	public void create(User user, String token) {
		 passwordResetTokenRepository.deleteByUser(user);
		PasswordResetToken passwordResetToken = new PasswordResetToken();
		passwordResetToken.setUser(user);
		passwordResetToken.setToken(token);
		passwordResetTokenRepository.save(passwordResetToken);
	    System.out.println("Password reset token created successfully.");

	}

	// トークンの文字列で検索した結果を返す
	public PasswordResetToken getPasswordResetToken(String token) {
		return passwordResetTokenRepository.findByToken(token);
	}
	
	 // パスワードリセットトークンを削除するメソッド (任意)
    @Transactional
    public void delete(PasswordResetToken token) {
        passwordResetTokenRepository.delete(token);
    }
    
    @Transactional
    public void deleteByUser(User user) {
        passwordResetTokenRepository.deleteByUser(user);
    }
}
