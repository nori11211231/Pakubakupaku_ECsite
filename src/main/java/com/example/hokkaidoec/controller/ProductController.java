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

	//	@GetMapping("/products")
	//	public String showList(Model model) {
	//		//商品一覧ページを表示する
	//
	//		List<Product> products = productMapper.findAll();
	//		model.addAttribute("products", products);
	//		return "products";
	//	}

	//商品詳細ページへ
	//	@GetMapping("/products/{id}")
	//	public String showDetail(@PathVariable("id") int id, Model model) {
	//		Product product = productMapper.findById(id);
	//		model.addAttribute("product", product);
	//		return "product-detail";
	//	}

	@GetMapping("/products/{productId}")
	public String detail(@PathVariable Integer productId, Model model) {

		// 商品本体
		Product product = productMapper.findById(productId);

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

		List<Product> products = productMapper.search(
				keyword, categoryId, regionId, minPrice, maxPrice, sort);

		model.addAttribute("products", products);
		return "products";
	}
}
