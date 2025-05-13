package com.example.NAGOYAMESHI.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.NAGOYAMESHI.entity.User;
import com.example.NAGOYAMESHI.form.SignupForm;
import com.example.NAGOYAMESHI.form.UserEditForm;
import com.example.NAGOYAMESHI.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {
	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public User create(SignupForm signupForm) {
		User user = new User();

		user.setName(signupForm.getName());
		user.setFurigana(signupForm.getFurigana());
		user.setPostalCode(signupForm.getPostalCode());
		user.setAddress(signupForm.getAddress());
		user.setPhoneNumber(signupForm.getPhoneNumber());
		user.setEmail(signupForm.getEmail());
		user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
		user.setEnabled(false);
		user.setPaidFlg(signupForm.isPaid()); // 有料会員フラグを設定
		user.setAdminFlg(signupForm.isAdmin()); // 管理者フラグを設定

		return userRepository.save(user);
	}

	@Transactional
	public void update(UserEditForm userEditForm) {
		User user = userRepository.getReferenceById(userEditForm.getId());

		user.setName(userEditForm.getName());
		user.setFurigana(userEditForm.getFurigana());
		user.setPostalCode(userEditForm.getPostalCode());
		user.setAddress(userEditForm.getAddress());
		user.setPhoneNumber(userEditForm.getPhoneNumber());
		user.setEmail(userEditForm.getEmail());

		userRepository.save(user);
	}

	public boolean isEmailRegistered(String email) {
		User user = userRepository.findByEmail(email);
		return user != null;
	}

	public boolean isSamePassword(String password, String passwordConfirmation) {
		return password.equals(passwordConfirmation);
	}

	@Transactional
	public void enableUser(User user) {
		user.setEnabled(true);
		userRepository.save(user);
	}

	public boolean isEmailChanged(UserEditForm userEditForm) {
		User currentUser = userRepository.getReferenceById(userEditForm.getId());
		return !userEditForm.getEmail().equals(currentUser.getEmail());
	}

	@Transactional
	public boolean resetPassword(String email, String newPassword) {
		User user = userRepository.findByEmail(email);
		if (user == null) {
			return false; // ユーザーが見つからない場合
		}
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);
		return true;
	}

	public boolean containsFullwidth(String str) {
		if (str == null || str.isEmpty()) {
			return false;
		}
		Pattern pattern = Pattern.compile("[^ -~｡-ﾟ]+"); // 半角カナ、半角英数字記号以外の文字
		Matcher matcher = pattern.matcher(str);
		return matcher.find();
	}
	
	@Transactional
	public void updatePassword(User user, String newPassword) {
	    user.setPassword(passwordEncoder.encode(newPassword));
	    userRepository.save(user);
	}
}
