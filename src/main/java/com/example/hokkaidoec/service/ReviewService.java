package com.example.hokkaidoec.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.hokkaidoec.entity.Review;
import com.example.hokkaidoec.mapper.ReviewMapper;

@Service
public class ReviewService {

	private final ReviewMapper reviewMapper;

	public ReviewService(ReviewMapper reviewMapper) {
		this.reviewMapper = reviewMapper;
	}

	public List<Review> getReviewsByProductId(int productId) {
		return reviewMapper.findByProductId(productId);
	}

	public Double getAverageRating(int productId) {
		return reviewMapper.getAverageRating(productId);
	}

	public int getReviewCount(int productId) {
		return reviewMapper.getReviewCount(productId);
	}

	public boolean canUserReview(int userId, int productId) {
		return reviewMapper.countPurchased(userId, productId) > 0;
	}

	public void addReview(Review review) {
		reviewMapper.insertReview(review);
	}
}
