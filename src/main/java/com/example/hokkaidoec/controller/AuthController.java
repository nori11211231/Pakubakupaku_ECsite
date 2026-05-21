package com.example.hokkaidoec.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.hokkaidoec.entity.User;
import com.example.hokkaidoec.form.LoginForm;
import com.example.hokkaidoec.mapper.UserMapper;

@Controller
public class AuthController {

	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public AuthController(UserMapper userMapper) {
		this.userMapper = userMapper;
	}

	/** ログイン画面を表示する */
	@GetMapping("/login")
	public String showLoginForm(Model model) {
		model.addAttribute("form", new LoginForm());
		return "login";
	}

	/** ログイン処理を行う */
	@PostMapping("/login")
	public String login(
			@Validated @ModelAttribute("form") LoginForm form,
			BindingResult bindingResult,
			HttpSession session,
			Model model) {

		if (bindingResult.hasErrors()) {
			return "login";
		}

		// DBからユーザを検索する
		User user = userMapper.findByEmail(form.getEmail());

		if (user == null || !passwordEncoder.matches(form.getPassword(), user.getPassword())) {
			model.addAttribute("loginError", "メールアドレスまたはパスワードが違います");
			return "login";
		}

		// ログイン成功：セッションにユーザ情報を保存する
		session.setAttribute("loginUser", user);
		return "redirect:/top";
	}

	/** ログアウト処理を行う */
	@PostMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/login";
	}

}