package com.example.hokkaidoec.controller;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.hokkaidoec.entity.Product;
import com.example.hokkaidoec.entity.User;
import com.example.hokkaidoec.mapper.ProductsMapper;

@Controller
public class HomeController {

	private final ProductsMapper productsMapper;

	public HomeController(ProductsMapper productsMapper) {
		this.productsMapper = productsMapper;
	}

	@GetMapping({ "/", "/top" })
	public String showTopPage(Model model, HttpSession session) {

		// ログインユーザーを取得
		User loginUser = (User) session.getAttribute("loginUser");
		model.addAttribute("loginUser", loginUser);

		// ヘッダーのポイント表示用
		if (loginUser != null) {
			model.addAttribute("userPoints", loginUser.getPoint());
		} else {
			model.addAttribute("userPoints", 0);
		}

		// 全商品を取得
		List<Product> allProducts = productsMapper.findAll();

		// トップページ用に先頭4件だけ表示
		List<Product> recommendedProducts = new ArrayList<>();

		if (allProducts != null) {
			for (int i = 0; i < allProducts.size() && i < 4; i++) {
				recommendedProducts.add(allProducts.get(i));
			}
		}

		model.addAttribute("recommendedProducts", recommendedProducts);

		return "top";
	}
}