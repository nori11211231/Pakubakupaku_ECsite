package com.example.hokkaidoec.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.hokkaidoec.entity.PointHistory;

@Mapper
public interface PointHistoryMapper {

	/**
	 * ポイント増減履歴を登録する
	 */
	void insert(PointHistory pointHistory);

	/**
	 * 特定ユーザーのポイント履歴を全件取得する
	 * ★ @Param("userEmail") を追加しました
	 */
	List<PointHistory> findByUserEmail(@Param("userEmail") String userEmail);

	/**
	 * 特定ユーザーのポイント履歴を、最新のものから指定件数だけ取得する
	 */
	List<PointHistory> findRecentByUserEmail(@Param("userEmail") String userEmail, @Param("limit") Integer limit);

	/**
	 * 最新履歴の取得
	 */
	List<PointHistory> findLatestByUserEmail(@Param("userEmail") String userEmail);

}