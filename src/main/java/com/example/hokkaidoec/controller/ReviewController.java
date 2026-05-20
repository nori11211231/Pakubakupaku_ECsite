package com.example.hokkaidoec.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.hokkaidoec.entity.Category;
import com.example.hokkaidoec.entity.Product;
import com.example.hokkaidoec.entity.Region;
import com.example.hokkaidoec.entity.Review;
import com.example.hokkaidoec.service.CategoryService;
import com.example.hokkaidoec.service.OrderService;
import com.example.hokkaidoec.service.ProductService;
import com.example.hokkaidoec.service.RegionService;
import com.example.hokkaidoec.service.ReviewService;

@Controller
@RequestMapping("/products")
public class ReviewController {

	private final ReviewService reviewService;
	private final ProductService productService;
	private final RegionService regionService;
	private final CategoryService categoryService;
	private final OrderService orderService;

	public ReviewController(
			ReviewService reviewService,
			ProductService productService,
			RegionService regionService,
			CategoryService categoryService,
			OrderService orderService) {

		this.reviewService = reviewService;
		this.productService = productService;
		this.regionService = regionService;
		this.categoryService = categoryService;
		this.orderService = orderService;
	}

	// ============================
	// 商品詳細ページ（レビュー含む）
	// ============================
	@GetMapping("/{productId}/reviews")
	public String showProductDetail(
			@PathVariable("productId") Integer productId,
			Model model,
			HttpSession session) {

		// 商品情報
		Product product = productService.getProductById(productId);
		Region region = regionService.getRegionById(product.getRegionId());
		Category category = categoryService.getCategoryById(product.getCategoryId());

		model.addAttribute("product", product);
		model.addAttribute("region", region);
		model.addAttribute("category", category);

		// レビュー一覧
		List<Review> reviews = reviewService.getReviewsByProductId(productId);
		model.addAttribute("reviews", reviews);

		// 平均評価
		Double averageRating = reviewService.getAverageRating(productId);
		model.addAttribute("averageRating", averageRating);

		// レビュー件数
		int reviewCount = reviewService.getReviewCount(productId);
		model.addAttribute("reviewCount", reviewCount);

		// ログインユーザー
		Integer userId = (Integer) session.getAttribute("userId");

		boolean canReview = false;
		Integer orderId = null;

		if (userId != null) {
			// 購入済みチェック（orderId を取得）
			orderId = orderService.getOrderIdIfPurchased(userId, productId);
			canReview = (orderId != null);
		}

		model.addAttribute("canReview", canReview);
		model.addAttribute("orderId", orderId);

		return "product-detail";
	}

	// ============================
	// レビュー投稿
	// ============================
	@PostMapping("/{productId}/reviews")
	public String addReview(
			@PathVariable("productId") Integer productId,
			@RequestParam("orderId") Integer orderId,
			@RequestParam("rating") Integer rating,
			@RequestParam("comment") String comment,
			HttpSession session) {

		Integer userId = (Integer) session.getAttribute("userId");

		if (userId == null) {
			return "redirect:/login";
		}

		// 購入済みチェック
		Integer purchasedOrderId = orderService.getOrderIdIfPurchased(userId, productId);
		if (purchasedOrderId == null) {
			return "redirect:/products/" + productId + "?error=not_purchased";
		}

		// レビュー作成
		Review review = new Review();
		review.setUserId(userId);
		review.setProductId(productId);
		review.setOrderId(orderId);
		review.setRating(rating);
		review.setComment(comment);

		reviewService.addReview(review);

		return "redirect:/products/" + productId + "#reviews";
	}
}
