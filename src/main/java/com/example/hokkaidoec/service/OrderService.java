package com.example.hokkaidoec.service;

import org.springframework.stereotype.Service;

import com.example.hokkaidoec.mapper.OrderMapper;

@Service
public class OrderService {

	private final OrderMapper orderMapper;

	public OrderService(OrderMapper orderMapper) {
		this.orderMapper = orderMapper;
	}

	public Integer getOrderIdIfPurchased(int userId, int productId) {
		return orderMapper.findOrderIdByUserAndProduct(userId, productId);
	}
}
