package com.example.hokkaidoec.entity;

import java.time.LocalDateTime;

public class AiGrowth {

	private Integer id;
	private Integer userId;
	private String name;
	private Integer growthStage;
	private String personality;
	private LocalDateTime updatedAt;
	private String charaImageUrl;

	public AiGrowth() {
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getGrowthStage() {
		return growthStage;
	}

	public void setGrowthStage(Integer growthStage) {
		this.growthStage = growthStage;
	}

	public String getPersonality() {
		return personality;
	}

	public void setPersonality(String personality) {
		this.personality = personality;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getCharaImageUrl() {
		return charaImageUrl;
	}

	public void setCharaImageUrl(String charaImageUrl) {
		this.charaImageUrl = charaImageUrl;
	}
}
