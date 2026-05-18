package com.example.hokkaidoec.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.hokkaidoec.entity.OrderItem;

@Mapper
public interface OrderItemMapper {

	// 注文明細を1件登録する
	int insert(OrderItem orderItem);

	// 注文IDから注文明細を取得する
	List<OrderItem> findByOrderId(@Param("orderId") Integer orderId);

	// 注文IDから商品名付きの注文明細を取得する
	List<Map<String, Object>> findDetailByOrderId(@Param("orderId") Integer orderId);
}