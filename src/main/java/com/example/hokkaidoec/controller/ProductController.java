package com.example.hokkaidoec.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.hokkaidoec.entity.Category;
import com.example.hokkaidoec.entity.Product;
import com.example.hokkaidoec.entity.Region;
import com.example.hokkaidoec.mapper.CategoryMapper;
import com.example.hokkaidoec.mapper.ProductsMapper;
import com.example.hokkaidoec.mapper.RegionMapper;

@Controller
public class ProductController {
	private final ProductsMapper productMapper;
	private final RegionMapper regionMapper;
	private final CategoryMapper categoryMapper;

	public ProductController(ProductsMapper productMapper, CategoryMapper categoryMapper, RegionMapper regionMapper) {
		this.productMapper = productMapper;
		this.regionMapper = regionMapper;
		this.categoryMapper = categoryMapper;
	}

	@GetMapping("/products/{productId}")
	public String detail(@PathVariable Integer productId, Model model) {

		// 商品本体
		Product product = productMapper.findById(productId);
		if (product == null) {
			// 本当に存在しない ID のとき
			model.addAttribute("errorMessage", "商品が見つかりません");
			// 一覧に戻す or 専用エラーページへ
			return "redirect:/products";
		}
		// カテゴリ名を取得
		Category category = categoryMapper.findById(product.getCategoryId());

		// 地域名を取得
		Region region = regionMapper.findById(product.getRegionId());

		// View に渡す
		model.addAttribute("product", product);
		model.addAttribute("category", category);
		model.addAttribute("region", region);

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
			Model model) {
		// 商品検索をするコード
		List<Product> products = productMapper.search(
				keyword, categoryId, regionId, minPrice, maxPrice, sort);

		List<Category> categories = categoryMapper.findAll();
		List<Region> regions = regionMapper.findAll();

		// 商品地域カテゴリ一覧
		model.addAttribute("products", products);
		model.addAttribute("categories", categories);
		model.addAttribute("regions", regions);

		// ★ 選択状態を保持
		model.addAttribute("keyword", keyword);
		model.addAttribute("categoryId", categoryId);
		model.addAttribute("regionId", regionId);
		model.addAttribute("minPrice", minPrice);
		model.addAttribute("maxPrice", maxPrice);
		model.addAttribute("sort", sort);
		return "products";
	}
}
