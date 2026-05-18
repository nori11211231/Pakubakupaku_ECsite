package com.example.hokkaidoec.mapper;

import java.util.List;

import com.example.hokkaidoec.entity.Review;

public interface ReviewMapper {
	List<Review> findByProductId(Integer productId);

	List<Review> findByUserId(Integer userId);

	Review findById(Integer id);

	void insert(Review review);

	void update(Review review);

	void logicalDelete(Integer id);

	Review findByUserIdAndProductIdAndOrderId(
			Integer userId,
			Integer productId,
			Integer orderId);
}
