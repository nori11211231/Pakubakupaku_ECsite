package com.example.hokkaidoec.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.hokkaidoec.entity.Order;
import com.example.hokkaidoec.entity.User;

@Mapper
public interface UserMapper {

	/** メールアドレスでユーザを検索する */
	@Select("SELECT * FROM users WHERE email = #{email}")
	User findByEmail(String email);

	/** ユーザを登録する */
	@Insert("INSERT INTO users (name,email,address,phone,password,point,total_purchase_amount,rank_id) VALUES (#{name}, #{email},#{address},#{phone}, #{password},0,0,#{rankId})")
	void insert(User user);

	// 登録済みですをだす。

	/** IDでユーザを検索する */
	@Select("SELECT * FROM users WHERE id = #{id}")
	User findById(Integer id);

	/** ユーザのポイントを更新する */
	@Update("UPDATE users SET point = #{point} WHERE id = #{id}")
	void updatePoint(@Param("id") Integer id, @Param("point") Integer point);

	Integer findRankIdByTotalPurchaseAmount(@Param("totalPurchaseAmount") Integer totalPurchaseAmount);

	void updateTotalPurchaseAmountAndRank(
			@Param("id") Integer id,
			@Param("totalPurchaseAmount") Integer totalPurchaseAmount,
			@Param("rankId") Integer rankId);

	/** ユーザーIDに紐づく注文履歴を取得する */
	@Select("SELECT id, total_amount, order_status, user_id, shipping_address, order_date, used_point, earned_point FROM orders WHERE user_id = #{userId} ORDER BY id DESC")
	List<Order> findOrdersByUserId(@Param("userId") Integer userId);
}