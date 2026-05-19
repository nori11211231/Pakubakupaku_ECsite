package com.example.hokkaidoec.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.hokkaidoec.form.UserForm;
import com.example.hokkaidoec.service.UserService;

@Controller
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/register")
	public String showForm(Model model) {
		model.addAttribute("form", new UserForm());
		return "register";
	}

	@PostMapping("/register")
	public String registerUser(@ModelAttribute UserForm form, Model model) {
		try {
			// 登録処理を実行
			userService.register(form);
			return "redirect:/login"; // 成功したらログイン画面へ

		} catch (IllegalArgumentException e) {
			// ★2. 重複エラーをキャッチして、画面にエラーメッセージを送る
			model.addAttribute("registerError", e.getMessage());
			return "register"; // 失敗したら登録画面に戻る
		}

	}
}