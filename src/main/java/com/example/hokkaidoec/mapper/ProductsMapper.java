package com.example.hokkaidoec.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.hokkaidoec.entity.Product;

@Mapper
public interface ProductsMapper {
	/** 全商品を取得する */
	List<Product> findAll();

	/** IDで商品を1件取得する */
	Product findById(@Param("id") int id);

	//	検索条件（カテゴリ、地域、価格帯、キーワード、並べ替え）を受け取るメソッド
	//	まだ理解はできていないので保留
	List<Product> search(
			@Param("keyword") String keyword,
			@Param("categoryId") Integer categoryId,
			@Param("regionId") Integer regionId,
			@Param("minPrice") Integer minPrice,
			@Param("maxPrice") Integer maxPrice,
			@Param("sort") String sort);
}
