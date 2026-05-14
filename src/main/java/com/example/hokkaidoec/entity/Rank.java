package com.example.hokkaidoec.entity;

public class Rank {
	private int id;
	private String name;
	private int min_amount;
	private double point_rate;

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

	public int getMin_amount() {
		return min_amount;
	}

	public void setMin_amount(int min_amount) {
		this.min_amount = min_amount;
	}

	public double getPoint_rate() {
		return point_rate;
	}

	public void setPoint_rate(double point_rate) {
		this.point_rate = point_rate;
	}

}