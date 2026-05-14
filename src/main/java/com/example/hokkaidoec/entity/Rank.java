package com.example.hokkaidoec.entity;

public class Rank {
	private int id;
	private String name;
	private int minAmount;
	private double pointRate;

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

	public int getMinAmount() {
		return minAmount;
	}

	public void setMinAmount(int minAmount) {
		this.minAmount = minAmount;
	}

	public double getPointRate() {
		return pointRate;
	}

	public void setPointRate(double pointRate) {
		this.pointRate = pointRate;
	}

}