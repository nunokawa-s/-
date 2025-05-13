package com.example.NAGOYAMESHI.form;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewPostForm {

	private Integer id;
	
	@NotNull(message = "評価を選択してください。")
	@Range(min = 1, max = 5, message = "評価は１から５を選択してください。")
	private Integer reviewScore;

	
	@NotBlank(message = "コメントを入力してください。")
	@Length(max = 200, message = "コメントは２００文字以内で入力してください。")
	private String reviewText;
}
