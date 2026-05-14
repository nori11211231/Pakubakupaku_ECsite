package com.example.hokkaidoec.entity;

import java.time.LocalDateTime;

public class PointHistory {
	private int id;
	private int point_change;
	private String reason;
	private LocalDateTime created_at;

	public PointHistory(int id, int point_change, String reason, LocalDateTime created_at) {
		this.id = id;
		this.point_change = point_change;
		this.reason = reason;
		this.created_at = LocalDateTime.now();
	}

	/**
	 * @return id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id セットする id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return point_change
	 */
	public int getPoint_change() {
		return point_change;
	}

	/**
	 * @param point_change セットする point_change
	 */
	public void setPoint_change(int point_change) {
		this.point_change = point_change;
	}

	/**
	 * @return reason
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * @param reason セットする reason
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}

	/**
	 * @return created_at
	 */
	public LocalDateTime getCreated_at() {
		return created_at;
	}

	/**
	 * @param created_at セットする created_at
	 */
	public void setCreated_at(LocalDateTime created_at) {
		this.created_at = created_at;
	}

}