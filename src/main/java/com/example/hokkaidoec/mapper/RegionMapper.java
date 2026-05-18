package com.example.hokkaidoec.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.hokkaidoec.entity.Region;

@Mapper
public interface RegionMapper {
	Region findById(Integer id);
}

//import java.util.List;
//
//import org.apache.ibatis.annotations.Mapper;
//
//@Mapper
//public class RegionMapper {
//	/** 全商品を取得する */
//	List<Reagion> findAll();
//
//	//findAllの内容を変更必須
//	/** IDで商品を1件取得する */
//	Reagion findById(int id);
//	//findByIdの内容を変更必須
//
//}
