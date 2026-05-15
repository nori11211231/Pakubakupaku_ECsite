package com.example.hokkaidoec.mapper;

import java.util.List;


import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import com.example.hokkaidoec.entity.GamePlayHistory;

@Mapper
public interface GamePlayHistoryMapper {

	// ガチャ結果を保存する
	void insert(GamePlayHistory history);

	// ユーザーごとのガチャ履歴を取得する
	List<GamePlayHistory> findByUserId(@Param("userId") Integer userId);
}