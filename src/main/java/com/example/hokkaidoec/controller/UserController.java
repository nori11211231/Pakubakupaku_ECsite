package com.example.hokkaidoec.controller;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.hokkaidoec.entity.User;
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
	public String showMypage(Model model, HttpSession session) {
		// 1. 【修正】(UserForm) ではなく (com.example.hokkaidoec.entity.User) でキャストする
		User loginUser = (User) session
				.getAttribute("loginUser");

		// セッションが空ならログイン画面へ
		if (loginUser == null) {
			return "redirect:/login";
		}

		// 2. 本物のユーザー情報をModelに登録
		// HTML側の `${user.name}` や `${user.email}` を読みに行きます
		model.addAttribute("user", loginUser);

		// 3. AIのダミーデータ（Map）
		Map<String, Object> dummyAi = new HashMap<>();
		dummyAi.put("level", 3);
		dummyAi.put("exp", 7800);//total→ブロンズ
		model.addAttribute("ai", dummyAi);

		// 4. 注文履歴のダミーデータ（必要であれば追加）
		// 前回のList<Map>をここに置いておくと、注文履歴もエラーにならず表示されます！★★★★

		return "mypage";
	}
}
