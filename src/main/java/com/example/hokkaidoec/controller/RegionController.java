package com.example.hokkaidoec.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.hokkaidoec.entity.User;

@Controller
public class RegionController {
	@GetMapping("/regions")
	public String showDetail(Model model, HttpSession session) {
		// ログインユーザー
		User loginUser = (User) session.getAttribute("loginUser");
		model.addAttribute("loginUser", loginUser);
		return "regions";
	}
}
