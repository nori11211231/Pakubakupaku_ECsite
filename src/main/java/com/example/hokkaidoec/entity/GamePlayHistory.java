package com.example.hokkaidoec.entity;

import java.time.LocalDateTime;

public class GamePlayHistory {

	private Integer id;
	private Integer userId;
	private Integer betPoint;
	private Boolean result;
	private String resultType;
	private Integer earnedPoint;
	private LocalDateTime playedAt;

	// 画面表示用
	private String resultText;
	private String videoPath;
	private String message;
	private Integer currentPoint;

	public GamePlayHistory() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getBetPoint() {
		return betPoint;
	}

	public void setBetPoint(Integer betPoint) {
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

	public Integer getEarnedPoint() {
		return earnedPoint;
	}

	public void setEarnedPoint(Integer earnedPoint) {
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

	public Integer getCurrentPoint() {
		return currentPoint;
	}

	public void setCurrentPoint(Integer currentPoint) {
		this.currentPoint = currentPoint;
	}
}