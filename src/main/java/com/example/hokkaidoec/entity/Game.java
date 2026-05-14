package com.example.hokkaidoec.entity;

import java.time.LocalDateTime;

public class Game {
	LocalDateTime today = LocalDateTime.now();
	private int id;
	private int bet_Point;
	private boolean result;
	private int earned_Point;
	private LocalDateTime played_At;

	public int getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getBet_Point() {
		return bet_Point;
	}

	public void setBet_Point(Integer betPoint) {
		this.bet_Point = betPoint;
	}

	public boolean getResult() {
		return result;
	}

	public void setResult(Boolean result) {
		this.result = result;
	}

	public int getEarned_Point() {
		return earned_Point;
	}

	public void setEarned_Point(Integer earnedPoint) {
		this.earned_Point = earnedPoint;
	}

	public LocalDateTime getPlayed_At() {

		return played_At;
	}

	public void setPlayed_At(LocalDateTime playedAt) {
		this.played_At = playedAt;
	}
}