package com.example.hokkaidoec.entity;

import java.time.LocalDateTime;

public class Ai {
	private int id;
	private String name;
	private String stage;
	private String personality;
	private LocalDateTime updatedAt;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
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

	//	public void setUpdatedAt(LocalDateTime updatedAt) {
	//		this.updatedAt = updatedAt;
	//	}
}