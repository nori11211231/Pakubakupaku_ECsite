package com.example.hokkaidoec.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.hokkaidoec.entity.Review;

@Mapper
public interface ReviewMapper {

	List<Review> findByProductId(int productId);

	Double getAverageRating(int productId);

	int getReviewCount(int productId);

	int countPurchased(int userId, int productId);

	void insertReview(Review review);
}
