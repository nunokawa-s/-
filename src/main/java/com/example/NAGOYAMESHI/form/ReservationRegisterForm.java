package com.example.NAGOYAMESHI.form;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReservationRegisterForm {

	private Integer shopId;
	
	private Integer userId;
	
	private String fromDateToReserve;
	
	private String fromTimeToReserve;
	
	private Integer numberOfPeople;
}
