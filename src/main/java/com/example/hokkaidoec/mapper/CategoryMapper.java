package com.example.hokkaidoec.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.hokkaidoec.entity.Category;

@Mapper
public interface CategoryMapper {

	/** 全商品を取得する */
	List<Category> findAll();

	Category findById(Integer id);

}

//@Mapper
//public class CategoryMapper {

//	//findAllの内容を変更必須
//
//	/** IDで商品を1件取得する */
//	Category findById(int id);
//	//findByIdの内容を変更必須
//}
