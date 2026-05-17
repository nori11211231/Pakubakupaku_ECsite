package com.example.hokkaidoec.entity;

import java.math.BigDecimal;

public class Rank {
	private Integer id;
	private String rankName;
	private Integer minAmount;
	private BigDecimal pointRate;

	public Rank() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getRankName() {
		return rankName;
	}

	public void setRankName(String rankName) {
		this.rankName = rankName;
	}

	public Integer getMinAmount() {
		return minAmount;
	}

	public void setMinAmount(Integer minAmount) {
		this.minAmount = minAmount;
	}

	public BigDecimal getPointRate() {
		return pointRate;
	}

	public void setPointRate(BigDecimal pointRate) {
		this.pointRate = pointRate;
	}
}
