package com.example.NAGOYAMESHI.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.NAGOYAMESHI.entity.User;
import com.example.NAGOYAMESHI.form.UserEditForm;
import com.example.NAGOYAMESHI.repository.UserRepository;
import com.example.NAGOYAMESHI.security.UserDetailsImpl;
import com.example.NAGOYAMESHI.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
	 private final UserRepository userRepository;    
	    private final UserService userService; 

	    public UserController(UserRepository userRepository, UserService userService) {
	        this.userRepository = userRepository;    
	        this.userService = userService; 

	    }    
	    
	    @GetMapping
	    public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {         
	        User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());  
	        
	        model.addAttribute("user", user);
	        
	        return "users/index";
	    }
	    
	    @GetMapping("/edit")
	    public String edit(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {        
	        User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());  
	        UserEditForm userEditForm = new UserEditForm(user.getId(), user.getName(), user.getFurigana(), user.getPostalCode(), user.getAddress(), user.getPhoneNumber(), user.getEmail());
	        
	        model.addAttribute("userEditForm", userEditForm);
	        
	        return "users/edit";
	    } 
	    
	    @PostMapping("/update")
	    public String update(@ModelAttribute @Validated UserEditForm userEditForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
	        
	        if (userService.isEmailChanged(userEditForm) && userService.isEmailRegistered(userEditForm.getEmail())) {
	            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "すでに登録済みのメールアドレスです。");
	            bindingResult.addError(fieldError);                       
	        }
	        if (    userService.containsFullwidth(userEditForm.getPostalCode()) ||
	                userService.containsFullwidth(userEditForm.getPhoneNumber()) ) {
	                redirectAttributes.addFlashAttribute("errorMessage", "全角文字は使用できません。半角で入力してください。");
	                return "users/edit"; // リダイレクトではなく編集画面を再度表示
	            }
	        
	        if (bindingResult.hasErrors()) {
	            return "user/edit";
	        }
	        
	        userService.update(userEditForm);
	        redirectAttributes.addFlashAttribute("successMessage", "会員情報を編集しました。");
	        
	        return "redirect:/user";
	    }    
	   
	    @GetMapping("/reset-password")
	    public String resetPasswordForm() {
	        return "users/password"; // パスワード再設定フォームのThymeleafテンプレート
	    }

	    @PostMapping("/reset-password")
	    public String resetPassword(@RequestParam String email,
	                                @RequestParam String new_password,
	                                @RequestParam String confirm_password,
	                                RedirectAttributes redirectAttributes) {

	        if (!userService.isSamePassword(new_password, confirm_password)) {
	            redirectAttributes.addFlashAttribute("errorMessage", "パスワードが一致しません。");
	            return "redirect:/user/reset-password"; // パスワードが一致しない場合
	        }

	        boolean result = userService.resetPassword(email, new_password);

	        if (!result) {
	            redirectAttributes.addFlashAttribute("errorMessage", "登録されていないメールアドレスです。");
	            return "redirect:/user/reset-password"; // メールアドレスが登録されていない場合
	        }

	        redirectAttributes.addFlashAttribute("successMessage", "パスワードが正常に再設定されました。");
	        return "redirect:/login"; // 成功時にリダイレクトする先
	    }
	    
	  
}
