package com.example.hokkaidoec.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.hokkaidoec.entity.PointHistory;
import com.example.hokkaidoec.entity.User;
import com.example.hokkaidoec.mapper.PointHistoryMapper;
import com.example.hokkaidoec.mapper.UserMapper;

@Controller
public class PointController {

	private final UserMapper userMapper;
	private final PointHistoryMapper pointHistoryMapper;

	public PointController(UserMapper userMapper, PointHistoryMapper pointHistoryMapper) {
		this.userMapper = userMapper;
		this.pointHistoryMapper = pointHistoryMapper;
	}

	/**
	 * ポイントページの表示
	 */
	@GetMapping("/points")
	public String showPointPage(HttpSession session, Model model) {

		User loginUser = (User) session.getAttribute("loginUser");

		if (loginUser == null) {
			model.addAttribute("errorMessage", "ログインが必要です。");
			return "redirect:login";
		}

		String userEmail = loginUser.getEmail();
		User currentUser = userMapper.findByEmail(userEmail);

		// ポイントページ用には直近の数件（最新履歴）を渡す
		List<PointHistory> latestHistory = pointHistoryMapper.findLatestByUserEmail(userEmail);

		model.addAttribute("user", currentUser);
		model.addAttribute("pointHistoryList", latestHistory);

		// HTML側（point.html）の表示崩れを防ぐためのダミー・補完データ
		// ※実際の実装フェーズに合わせてAiGrowth系から取得する形に変更してください
		Map<String, Object> dummyAi = new HashMap<>();
		dummyAi.put("level", 3);
		dummyAi.put("exp", 7800);
		model.addAttribute("aiGrowth", dummyAi);
		model.addAttribute("nextLevelRequiredAmount", 2200);

		return "point";
	}

	/**
	 * ポイント履歴全件ページの表示（新設）
	 */
	@GetMapping("/points/history")
	public String showPointHistoryPage(HttpSession session, Model model) {

		User loginUser = (User) session.getAttribute("loginUser");

		if (loginUser == null) {
			model.addAttribute("errorMessage", "ログインが必要です。");
			return "login";
		}

		String userEmail = loginUser.getEmail();

		// 履歴画面用に全件取得メソッド（findByUserEmail）を呼び出す
		List<PointHistory> allHistory = pointHistoryMapper.findByUserEmail(userEmail);
		model.addAttribute("pointHistoryList", allHistory);

		return "point-history"; // point-history.html を呼び出す
	}
}