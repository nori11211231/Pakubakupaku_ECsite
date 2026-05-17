package com.example.hokkaidoec.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.hokkaidoec.entity.Product;
import com.example.hokkaidoec.mapper.ProductMapper;

@Controller
public class ProductController {
	private final ProductMapper productMapper;

	public ProductController(ProductMapper productMapper) {
		this.productMapper = productMapper;
	}

	@GetMapping("/products")
	public String showList(Model model) {
		//商品一覧ページを表示する

		List<Product> products = productMapper.findAll();
		model.addAttribute("products", products);
		return "products";
	}

	@GetMapping("/products/{id}")
	public String showDetail(@PathVariable("id") int id, Model model) {
		Product product = productMapper.findById(id);
		model.addAttribute("product", product);
		return "product-detail";
	}
	//	この書き方がいいらしいがまだ理解できていないので保留
	//	 @GetMapping("/products")
	//	    public String showList(
	//	        @RequestParam(value = "keyword", required = false) String keyword,
	//	        @RequestParam(value = "categoryId", required = false) Integer categoryId,
	//	        @RequestParam(value = "regionId", required = false) Integer regionId,
	//	        @RequestParam(value = "minPrice", required = false) Integer minPrice,
	//	        @RequestParam(value = "maxPrice", required = false) Integer maxPrice,
	//	        @RequestParam(value = "sort", required = false) String sort,
	//	        Model model
	//	    ) {
	//
	//	        List<Product> products = productMapper.search(
	//	            keyword, categoryId, regionId, minPrice, maxPrice, sort
	//	        );
	//
	//	        model.addAttribute("products", products);
	//	        return "product/list";
	//	    }
	//
	//	    @GetMapping("/products/{id}")
	//	    public String showDetail(@PathVariable("id") int id, Model model) {
	//	        Product product = productMapper.findById(id);
	//	        model.addAttribute("product", product);
	//	        return "product/detail";
	//	    }
	//	}
}
