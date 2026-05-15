package com.example.hokkaidoec.entity;

import java.math.BigDecimal;

public class Rank {
	public int rankId;
	public String rankName;
	public int minAmount;
	public BigDecimal pointRate;

	public Rank() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public int getRankId() {
		return rankId;
	}

	public void setRankId(int rankId) {
		this.rankId = rankId;
	}

	public String getRankName() {
		return rankName;
	}

	public void setRankName(String rankName) {
		this.rankName = rankName;
	}

	public int getMinAmount() {
		return minAmount;
	}

	public void setMinAmount(int minAmount) {
		this.minAmount = minAmount;
	}

	public BigDecimal getPointRate() {
		return pointRate;
	}

	public void setPointRate(BigDecimal pointRate) {
		this.pointRate = pointRate;
	}

}
