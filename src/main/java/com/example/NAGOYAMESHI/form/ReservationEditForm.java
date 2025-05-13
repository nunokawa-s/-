package com.example.NAGOYAMESHI.form;

import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class ReservationEditForm {
    
	public ReservationEditForm(Integer shopId, Integer userId, String fromDateToReserve, String fromTimeToReserve, Integer numberOfPeople) {
	        this.shopId = shopId;
	        this.userId = userId;
	        this.fromDateToReserve = fromDateToReserve;
	        this.fromTimeToReserve = fromTimeToReserve;
	        this.numberOfPeople = numberOfPeople;
	    }	
	private Integer id;
	
	private Integer shopId;
	
	private Integer userId;

	@NotBlank(message = "予約日を選択してください。")
	private String fromDateToReserve;

	@NotBlank(message = "予約する時間を選択してください。")
	private String fromTimeToReserve;
	
	@NotNull(message = "人数を入力してください。")
	@Range(min = 1,  message = "人数は１以上に設定してください。")
	private Integer numberOfPeople;
}