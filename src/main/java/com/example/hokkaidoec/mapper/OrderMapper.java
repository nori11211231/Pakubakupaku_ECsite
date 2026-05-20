package com.example.hokkaidoec.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.hokkaidoec.entity.Order;

@Mapper
public interface OrderMapper {

	// 注文を1件登録する
	int insert(Order order);

	// 注文IDから注文情報を取得する
	Order findById(@Param("id") Integer id);

	// ユーザーIDから注文履歴を取得する
	List<Order> findByUserId(@Param("userId") Integer userId);

	//注文詳細Mapは何かわからないから聞く
	List<Map<String, Object>> findDetailsByOrderId(int orderId);

	//注文したやつを一件取得
	Order findById(int orderId);

}