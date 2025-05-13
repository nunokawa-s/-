package com.example.NAGOYAMESHI.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.NAGOYAMESHI.entity.PasswordResetToken;
import com.example.NAGOYAMESHI.entity.User;
import com.example.NAGOYAMESHI.entity.VerificationToken;
import com.example.NAGOYAMESHI.event.PasswordResetEventPublisher;
import com.example.NAGOYAMESHI.event.SignupEventPublisher;
import com.example.NAGOYAMESHI.form.RequestForm;
import com.example.NAGOYAMESHI.form.SignupForm;
import com.example.NAGOYAMESHI.service.PasswordResetTokenService;
import com.example.NAGOYAMESHI.service.UserService;
import com.example.NAGOYAMESHI.service.VerificationTokenService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AuthController {
	private final UserService userService;
	private final SignupEventPublisher signupEventPublisher;
	private final VerificationTokenService verificationTokenService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final PasswordResetEventPublisher passwordResetEventPublisher;


	
	public AuthController(UserService userService, SignupEventPublisher signupEventPublisher,PasswordResetEventPublisher passwordResetEventPublisher,
			VerificationTokenService verificationTokenService, PasswordResetTokenService passwordResetTokenService) {
		this.userService = userService;
		this.signupEventPublisher = signupEventPublisher;
		this.verificationTokenService = verificationTokenService;
		this.passwordResetTokenService = passwordResetTokenService;
		this.passwordResetEventPublisher= passwordResetEventPublisher;

	}
	
	

	@GetMapping("/login")
	public String login() {
		return "auth/login";
	}

	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("signupForm", new SignupForm());
		return "auth/signup";
	}

	@PostMapping("/signup")
	public String signup(@ModelAttribute @Validated SignupForm signupForm, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest) {

		if (userService.isEmailRegistered(signupForm.getEmail())) {
			FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "すでに登録済みのメールアドレスです。");
			bindingResult.addError(fieldError);
		}

		if (!userService.isSamePassword(signupForm.getPassword(), signupForm.getPasswordConfirmation())) {
			FieldError fieldError = new FieldError(bindingResult.getObjectName(), "password", "パスワードが一致しません。");
			bindingResult.addError(fieldError);
		}

		if (bindingResult.hasErrors()) {
			return "auth/signup";
		}

		User createdUser = userService.create(signupForm);
		String requestUrl = new String(httpServletRequest.getRequestURL());
		signupEventPublisher.publishSignupEvent(createdUser, requestUrl);
		redirectAttributes.addFlashAttribute("successMessage",
				"ご入力いただいたメールアドレスに認証メールを送信しました。メールに記載されているリンクをクリックし、会員登録を完了してください。");

		return "redirect:/";
	}

	@GetMapping("/signup/verify")
	public String verify(@RequestParam(name = "token") String token, Model model) {
		VerificationToken verificationToken = verificationTokenService.getVerificationToken(token);

		if (verificationToken != null) {
			User user = verificationToken.getUser();
			userService.enableUser(user);
			String successMessage = "会員登録が完了しました。";
			model.addAttribute("successMessage", successMessage);
		} else {
			String errorMessage = "トークンが無効です。";
			model.addAttribute("errorMessage", errorMessage);
		}

		return "auth/verify";
	}
	
	@GetMapping("/request")
	public String request(Model model) {
		model.addAttribute("requestForm", new RequestForm());
		return "auth/password-reset-request";

	}
	
	 @PostMapping("/request")
	    public String handlePasswordResetRequest(@RequestParam("email") String email, Model model, HttpServletRequest httpServletRequest) {
	        User user = passwordResetTokenService.findUserByEmail(email);

	        if (user == null) {
	            model.addAttribute("error", "登録されていないメールアドレスです。");
	            return "auth/password-reset-request";
	        }

	        System.out.println("User found: " + user.getId() + ", " + user.getEmail()); // ログ出力

	        String requestUrl = httpServletRequest.getRequestURL().toString();
	        passwordResetEventPublisher.publishPasswordResetEvent(user, requestUrl);

	        model.addAttribute("message", "パスワード再設定用のメールを送信しました。ご確認ください。");
	        return "auth/passwordResetConfirmation"; // メール送信完了画面
	    }
	
	 @GetMapping("/reset-password/form")
	    public String showPasswordResetForm(@RequestParam("token") String token, Model model) {
	        System.out.println("Token received in showPasswordResetForm: " + token); // ← ここに追加
 
		 
		 PasswordResetToken passwordResetToken = passwordResetTokenService.getPasswordResetToken(token);

	        if (passwordResetToken == null) {
	            model.addAttribute("error", "無効なパスワード再設定トークンです。");
	            return "auth/password-reset-error"; // エラー画面
	        }

	        model.addAttribute("token", token);
	        return "auth/reset-password";
	    }

	    @PostMapping("/reset-password")
	    public String updatePassword(@RequestParam("token") String token,
	                                 @RequestParam("newPassword") String newPassword,
	                                 @RequestParam("confirmPassword") String confirmPassword,
	                                 Model model) {

	        if (!newPassword.equals(confirmPassword)) {
	            model.addAttribute("error", "新しいパスワードと確認用パスワードが一致しません。");
	            model.addAttribute("token", token);
	            return "auth/reset-password-form";
	        }

	        PasswordResetToken passwordResetToken = passwordResetTokenService.getPasswordResetToken(token);

	        if (passwordResetToken == null) {
	            model.addAttribute("error", "無効なパスワード再設定トークンです。");
	            return "auth/password-reset-error"; // エラー画面
	        }

	        User user = passwordResetToken.getUser();
	        userService.updatePassword(user, newPassword);

	        // パスワードリセットトークンを削除 (任意)
	        passwordResetTokenService.delete(passwordResetToken);

	        model.addAttribute("message", "パスワードが正常に更新されました。");
	        return "auth/password-reset-success"; // パスワード更新成功画面
	    }
	}
	
