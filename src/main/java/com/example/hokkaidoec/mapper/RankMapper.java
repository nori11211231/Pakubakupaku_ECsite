package com.example.hokkaidoec.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.example.hokkaidoec.entity.Rank;

@Mapper
public interface RankMapper {

	// @Selectなどのアノテーションは全部消して、メソッドの定義だけにします！
	List<Rank> findAll();

	@Select("SELECT id, rank_name AS rankName, min_amount AS minAmount, point_rate AS pointRate FROM ranks WHERE id = #{id}")
	Rank findById(Integer id);

	void insert(Rank rank);

	void update(Rank rank);

	void delete(Integer id);

	Rank selectById(Integer rankId);
}