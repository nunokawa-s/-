package com.example.NAGOYAMESHI.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestForm {

	@NotNull(message = "メールアドレスを入力してください。")
	@Email(message = "メールアドレスは正しい形式で入力してください。")
	private String email;
}
