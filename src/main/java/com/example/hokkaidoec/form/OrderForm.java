package com.example.hokkaidoec.form;

public class OrderForm {

	/** 商品詳細画面から注文に追加するときの商品ID */
	private Integer productId;

	/** 注文する数量 */
	private Integer quantity;

	/** 配送先住所 */
	private String shippingAddress;

	/** 使用ポイント */
	private Integer usedPoint;

	public OrderForm() {
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public Integer getUsedPoint() {
		return usedPoint;
	}

	public void setUsedPoint(Integer usedPoint) {
		this.usedPoint = usedPoint;
	}
}