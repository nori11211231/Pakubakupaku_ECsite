package com.example.hokkaidoec.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.hokkaidoec.entity.AiGrowth;

@Mapper
public interface AiGrowthMapper {

	AiGrowth findByUserId(Integer userId);

	void insert(AiGrowth aiGrowth);

	void update(AiGrowth aiGrowth);

	void updateGrowthStage(
			@Param("userId") Integer userId,
			@Param("growthStage") Integer growthStage);

	Map<String, Object> findAiStatusByUserId(@Param("userId") Integer userId);
}