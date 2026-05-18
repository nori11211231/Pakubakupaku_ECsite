package com.example.hokkaidoec.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.hokkaidoec.entity.GamePlayHistory;

@Mapper
public interface GamePlayHistoryMapper {

	void insert(GamePlayHistory gamePlayHistory);

	List<GamePlayHistory> findByUserId(Integer userId);

	GamePlayHistory findLatestByUserId(Integer userId);

}