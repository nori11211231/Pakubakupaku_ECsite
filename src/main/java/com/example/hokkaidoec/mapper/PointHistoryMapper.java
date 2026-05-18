package com.example.hokkaidoec.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.hokkaidoec.entity.PointHistory;

@Mapper
public interface PointHistoryMapper {

	/**
	 * ポイント増減履歴を登録する
	 * （注文完了時、スロットプレイ時、ミッション達成時などに他担当からも呼ばれます）
	 */
	void insert(PointHistory pointHistory);

	/**
	 * 特定ユーザーのポイント履歴を全件取得する（ポイント履歴画面用）
	 */
	List<PointHistory> findByUserId(String Email);

	/**
	 * 特定ユーザーのポイント履歴を、最新のものから指定件数だけ取得する（ポイントページの一部表示用）
	 */
	List<PointHistory> findRecentByUserEmail(@Param("userEmail") String userEmail, @Param("limit") Integer limit);

	List<PointHistory> findLatestByUserEmail(@Param("userEmail") String userEmail);

}