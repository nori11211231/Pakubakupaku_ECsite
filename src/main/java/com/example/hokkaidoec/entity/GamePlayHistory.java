package com.example.hokkaidoec.entity;

import java.time.LocalDateTime;

public class GamePlayHistory {

	private int id;
	private int userId;
	private int betPoint;
	private Boolean result;
	private String resultType;
	private int earnedPoint;
	private LocalDateTime playedAt;

	// 画面表示用
	private String resultText;
	private String videoPath;
	private String message;
	private int currentPoint;

	public GamePlayHistory() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getBetPoint() {
		return betPoint;
	}

	public void setBetPoint(int betPoint) {
		this.betPoint = betPoint;
	}

	public Boolean getResult() {
		return result;
	}

	public void setResult(Boolean result) {
		this.result = result;
	}

	public String getResultType() {
		return resultType;
	}

	public void setResultType(String resultType) {
		this.resultType = resultType;
	}

	public int getEarnedPoint() {
		return earnedPoint;
	}

	public void setEarnedPoint(int earnedPoint) {
		this.earnedPoint = earnedPoint;
	}

	public LocalDateTime getPlayedAt() {
		return playedAt;
	}

	public void setPlayedAt(LocalDateTime playedAt) {
		this.playedAt = playedAt;
	}

	public String getResultText() {
		return resultText;
	}

	public void setResultText(String resultText) {
		this.resultText = resultText;
	}

	public String getVideoPath() {
		return videoPath;
	}

	public void setVideoPath(String videoPath) {
		this.videoPath = videoPath;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getCurrentPoint() {
		return currentPoint;
	}

	public void setCurrentPoint(int currentPoint) {
		this.currentPoint = currentPoint;
	}

}
