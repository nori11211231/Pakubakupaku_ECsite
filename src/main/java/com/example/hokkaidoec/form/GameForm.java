package com.example.hokkaidoec.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class GameForm {

	@NotNull(message = "ベットポイントは必須です")
	@Min(value = 1, message = "ベットポイントは1以上で入力してください")
	private int betPoint;

	public int getBetPoint() {
		return betPoint;
	}

	public void setBetPoint(int betPoint) {
		this.betPoint = betPoint;
	}
}
