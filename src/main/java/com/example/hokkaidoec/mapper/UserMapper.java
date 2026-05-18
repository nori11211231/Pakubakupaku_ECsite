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
	@Insert("INSERT INTO users (name,email,address,phone,password,point,totalPurchaseAmount,rankId) VALUES (#{name}, #{email},#{address},#{phone}, #{password},0,0,#{rankId})")
	void insert(User user);
}

//★★★Userserviceに書いておく
////新規ユーザーオブジェクトを作成
//User newUser = new User();
//newUser.setName(form.getName());
////... 他のプロパティをセット ...
//
////初期ランクのID（例: 1 = 一般会員）を設定
//newUser.setRankId(1); 
//
////マッパーを呼び出して保存
//userMapper.insert(newUser);
