package com.example.hokkaidoec.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.hokkaidoec.entity.Rank;

@Mapper
public interface RankMapper {

	// @Selectなどのアノテーションは全部消して、メソッドの定義だけにします！
	List<Rank> findAll();

	Rank findById(Integer id);

	void insert(Rank rank);

	void update(Rank rank);

	void delete(Integer id);
}