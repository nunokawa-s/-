package com.example.NAGOYAMESHI.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserEditForm {
	@NotNull
	private Integer id;

	@NotBlank(message = "氏名を入力してください。")
	private String name;

	@NotBlank(message = "フリガナを入力してください。")
	private String furigana;

	@NotBlank(message = "郵便番号を入力してください。")
	@Pattern(regexp = "^[0-9]{7}$", message = "郵便番号はハイフンなしで半角数字7桁で入力してください。")
	private String postalCode;

	@NotBlank(message = "住所を入力してください。")
	private String address;

	@NotBlank(message = "電話番号を入力してください。")
	@Pattern(regexp = "^[0-9]{10,11}$", message = "電話番号はハイフンなしで半角数字10桁または11桁で入力してください。")
	private String phoneNumber;

	@NotBlank(message = "メールアドレスを入力してください。")
	private String email;
}