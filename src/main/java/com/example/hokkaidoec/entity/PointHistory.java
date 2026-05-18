package com.example.hokkaidoec.entity;

import java.time.LocalDateTime;

public class PointHistory {
	private Integer id;
	private Integer userId;
	private Integer pointChange;
	private String reason;
	private LocalDateTime createdAt;

	public PointHistory() {
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

	public Integer getPointChange() {
		return pointChange;
	}

	public void setPointChange(Integer pointChange) {
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
