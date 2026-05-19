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
	// 📁 @ModelAttribute に ("form") を追加するだけ！
	public String registerUser(@ModelAttribute("form") UserForm form, Model model) {
		try {
			// 登録処理を実行
			userService.register(form);
			return "redirect:/login";// 成功したらログイン画面へ

		} catch (IllegalArgumentException e) {
			// これで自動的に "form" という名前で入力中の中身がHTMLに返るようになります
			model.addAttribute("registerError", e.getMessage());
			return "register";
		}
	}

	@GetMapping("/mypage")
	public String showMypage(Model model) {
		// 本来はログイン中のユーザー情報をサービスから取得します
		// User面、AI面、注文履歴面のオブジェクトをModelに登録する

		// 例：
		// model.addAttribute("user", loginUser);
		// model.addAttribute("ai", userAiData);
		// model.addAttribute("orders", orderHistoryList);

		return "mypage";
	}
}
