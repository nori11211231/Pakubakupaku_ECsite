package com.example.hokkaidoec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.hokkaidoec.entity.AiGrowth;

@Mapper
public interface AiGrowthMapper {

	// ユーザーIDからAI成長情報を1件取得する
	AiGrowth findByUserId(@Param("userId") Integer userId);

	// 新規登録時にAI成長情報を作成する
	void insert(AiGrowth aiGrowth);

	// 注文後などにAIの成長段階を更新する
	void updateGrowthStage(
			@Param("userId") Integer userId,
			@Param("growthStage") Integer growthStage,
			@Param("charaImageUrl") String charaImageUrl);
}