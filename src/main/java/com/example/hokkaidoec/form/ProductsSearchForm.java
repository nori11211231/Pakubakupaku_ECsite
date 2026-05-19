package com.example.hokkaidoec.form;

import jakarta.validation.constraints.Size;

public class ProductsSearchForm {
	private String keyword;

	private Integer categoryId;

	private Integer regionId;
	@Size(min = -1, message = "数値は０以上で入力してください")
	private Integer minPrice;
	@Size(min = -1, message = "数値は０以上で入力してください")
	private Integer maxPrice;

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public Integer getRegionId() {
		return regionId;
	}

	public void setRegionId(Integer regionId) {
		this.regionId = regionId;
	}

	public Integer getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(Integer minPrice) {
		this.minPrice = minPrice;
	}

	public Integer getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(Integer maxPrice) {
		this.maxPrice = maxPrice;
	}

}
