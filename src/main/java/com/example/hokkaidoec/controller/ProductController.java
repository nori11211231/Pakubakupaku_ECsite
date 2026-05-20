package com.example.hokkaidoec.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.hokkaidoec.entity.Category;
import com.example.hokkaidoec.entity.Product;
import com.example.hokkaidoec.entity.Region;
import com.example.hokkaidoec.entity.User;
import com.example.hokkaidoec.mapper.CategoryMapper;
import com.example.hokkaidoec.mapper.ProductsMapper;
import com.example.hokkaidoec.mapper.RegionMapper;
import com.example.hokkaidoec.service.OrderService;
import com.example.hokkaidoec.service.ReviewService;

@Controller
public class ProductController {

	private final ProductsMapper productMapper;
	private final RegionMapper regionMapper;
	private final CategoryMapper categoryMapper;
	private final ReviewService reviewService; // ★追加
	private final OrderService orderService;

	public ProductController(
			ProductsMapper productMapper,
			CategoryMapper categoryMapper,
			RegionMapper regionMapper,
			ReviewService reviewService,
			OrderService orderService) { // ★追加
		this.productMapper = productMapper;
		this.regionMapper = regionMapper;
		this.categoryMapper = categoryMapper;
		this.reviewService = reviewService; // ★追加
		this.orderService = orderService;
	}

	@GetMapping("/products/{productId}")
	public String detail(@PathVariable Integer productId, Model model, HttpSession session) {

		// ログインユーザー
		User loginUser = (User) session.getAttribute("loginUser");
		model.addAttribute("loginUser", loginUser);

		// 商品取得
		Product product = productMapper.findById(productId);
		if (product == null) {
			model.addAttribute("errorMessage", "商品が見つかりません");
			return "redirect:/products";
		}

		// カテゴリ・地域
		Category category = categoryMapper.findById(product.getCategoryId());
		Region region = regionMapper.findById(product.getRegionId());

		model.addAttribute("product", product);
		model.addAttribute("category", category);
		model.addAttribute("region", region);
		Integer userId = (loginUser != null) ? loginUser.getId() : null;
		boolean canReview = false;
		Integer orderId = null;

		if (userId != null) {
			// 購入済みなら orderId が返る
			orderId = orderService.getOrderIdIfPurchased(userId, productId);
			canReview = (orderId != null);
		}

		model.addAttribute("canReview", canReview);
		model.addAttribute("orderId", orderId);

		return "product-detail";
	}

	@GetMapping("/products")
	public String showList(
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "categoryId", required = false) Integer categoryId,
			@RequestParam(value = "regionId", required = false) Integer regionId,
			@RequestParam(value = "minPrice", required = false) Integer minPrice,
			@RequestParam(value = "maxPrice", required = false) Integer maxPrice,
			@RequestParam(value = "sort", required = false) String sort,
			Model model, HttpSession session) {

		// ログインユーザー
		User loginUser = (User) session.getAttribute("loginUser");
		model.addAttribute("loginUser", loginUser);

		// 商品検索
		List<Product> products = productMapper.search(
				keyword, categoryId, regionId, minPrice, maxPrice, sort);

		List<Category> categories = categoryMapper.findAll();
		List<Region> regions = regionMapper.findAll();

		model.addAttribute("products", products);
		model.addAttribute("categories", categories);
		model.addAttribute("regions", regions);

		// 選択状態保持
		model.addAttribute("keyword", keyword);
		model.addAttribute("categoryId", categoryId);
		model.addAttribute("regionId", regionId);
		model.addAttribute("minPrice", minPrice);
		model.addAttribute("maxPrice", maxPrice);
		model.addAttribute("sort", sort);

		return "products";
	}
}
