package com.example.NAGOYAMESHI.form;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResetPasswordForm {
	private Integer userId;
	
	  private String password; 
}
