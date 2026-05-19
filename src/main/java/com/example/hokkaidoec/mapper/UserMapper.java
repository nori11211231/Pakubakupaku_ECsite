package com.example.hokkaidoec.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.example.hokkaidoec.entity.User;

@Mapper
public interface UserMapper {

	/** メールアドレスでユーザを検索する */
	@Select("SELECT * FROM users WHERE email = #{email}")
	User findByEmail(String email);

	/** ユーザを登録する */
	@Insert("INSERT INTO users (name,email,address,phone,password,point,total_purchase_amount,rank_id) VALUES (#{name}, #{email},#{address},#{phone}, #{password},0,0,#{rankId})")
	void insert(User user);

	//	登録済みですをだす。
}