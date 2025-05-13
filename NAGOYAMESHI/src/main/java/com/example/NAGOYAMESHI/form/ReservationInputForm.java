package com.example.NAGOYAMESHI.form;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservationInputForm {
	@NotBlank(message = "予約日を選択してください。")
	private String fromDateToReserve;

	@NotBlank(message = "予約する時間を選択してください。")
	private String fromTimeToReserve;

	@NotNull(message = "人数を入力してください。")
	@Min(value = 1, message = "人数は1人以上に設定してください。")
	private Integer numberOfPeople;

	public LocalDate getReservedDate() {
		
		return LocalDate.parse(fromDateToReserve);
	}

	public LocalTime getReservedTime() {
	
		return LocalTime.parse(fromTimeToReserve);
	}


	}

