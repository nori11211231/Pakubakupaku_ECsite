//package com.example.hokkaidoec.mapper;
//
//import java.util.List;
//
//import org.apache.ibatis.annotations.Delete;
//import org.apache.ibatis.annotations.Insert;
//import org.apache.ibatis.annotations.Mapper;
//import org.apache.ibatis.annotations.Options;
//import org.apache.ibatis.annotations.Select;
//import org.apache.ibatis.annotations.Update;
//
//import com.example.hokkaidoec.entity.Rank;
//
//@Mapper
//public interface RankMapper {
//
//	// 1. 全件取得（ランク一覧を表示するとき用）
//	// データベースの列名（スネークケース）を、Entityの変数名（キャメルケース）に自動マッピングします
//	@Select("SELECT id,rankName,minAmount, pointRate FROM ranks ORDER BY minAmount ASC")
//	List<Rank> findAll();
//
//	// 2. 1件取得（特定のランク情報を取得するとき用）
//	@Select("SELECT id,rankName, min_amount AS minAmount, point_rate AS pointRate FROM ranks WHERE id = #{id}")
//	Rank findById(Integer id);
//
//	// 3. 新規追加（新しいランクを作るとき用）
//	// @Options をつけると、データベース側で自動採番(SERIAL等)されたIDが、引数の rank オブジェクトの id に自動セットされます
//	@Insert("INSERT INTO ranks (rank_name, min_amount, point_rate) VALUES (#{rankName}, #{minAmount}, #{pointRate})")
//	@Options(useGeneratedKeys = true, keyProperty = "id")
//	void insert(Rank rank);
//
//	// 4. 更新（ランクの条件やポイント率を変更するとき用）
//	@Update("UPDATE ranks SET rank_name = #{rankName}, min_amount = #{minAmount}, point_rate = #{pointRate} WHERE id = #{id}")
//	void update(Rank rank);
//
//	// 5. 削除（ランクを消すとき用）
//	@Delete("DELETE FROM ranks WHERE id = #{id}")
//	void delete(Integer id);
//}