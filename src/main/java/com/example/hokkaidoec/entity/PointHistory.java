package com.example.hokkaidoec.entity;

import java.time.LocalDateTime;

public class PointHistory {
	private int id;
	private int pointChange;
	private String reason;
	private LocalDateTime createdAt;

	public PointHistory(){
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPointChange() {
		return pointChange;
	}

	public void setPointChange(int pointChange) {
		this.pointChange = pointChange;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

}
