package com.example.hokkaidoec.entity;

import java.time.LocalDateTime;

public class Game {
	LocalDateTime today;
	private int id;
	private int betPoint;
	private boolean result;
	private int earnedPoint;
	private LocalDateTime playedAt;

	public int getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getBetPoint() {
		return betPoint;
	}

	public void setBetPoint(Integer betPoint) {
		this.betPoint = betPoint;
	}

	public boolean getResult() {
		return result;
	}

	public void setResult(Boolean result) {
		this.result = result;
	}

	public int getEarnedPoint() {
		return earnedPoint;
	}

	public void setEarnedPoint(Integer earnedPoint) {
		this.earnedPoint = earnedPoint;
	}

	public LocalDateTime getPlayed_At() {

		return playedAt;
	}

	public void setPlayed_At(LocalDateTime playedAt) {
		this.playedAt = playedAt;
	}

	public void setTodayTime(LocalDateTime day) {
		this.today = day;
	}
}