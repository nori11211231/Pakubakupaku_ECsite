//package com.example.hokkaidoec.controller;
//
//import java.util.List;
//
//import jakarta.servlet.http.HttpSession;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import com.example.hokkaidoec.entity.PointHistory;
//import com.example.hokkaidoec.entity.User;
//// ↓★★【追加】他メンバーが作ったMapperをインポートする★★
//import com.example.hokkaidoec.mapper.PointHistoryMapper;
//import com.example.hokkaidoec.mapper.UserMapper;
//
//@Controller
//@RequestMapping("/points") // ポイント関連のパスを指定
//public class PointController {
//
//	// 責任者ルールに基づき、既存のMapperをインジェクションして利用 [cite: 37]
//	private final UserMapper userMapper;
//	private final PointHistoryMapper pointHistoryMapper;
//
//	// コンストラクタインジェクション
//	public PointController(UserMapper userMapper, PointHistoryMapper pointHistoryMapper) {
//		this.userMapper = userMapper;
//		this.pointHistoryMapper = pointHistoryMapper;
//	}
//
//	/**
//	 * ポイントページの表示
//	 */
//	@GetMapping
//	public String showPointPage(HttpSession session, Model model) {
//
//		// 1. セッション等からログイン中のユーザーIDを取得
//		User loginUser = (User) session.getAttribute("loginUser");
//
//		// 未ログイン時のエラーハンドリング（最終チェックリストの条件） [cite: 39]
//		if (loginUser == null) {
//			model.addAttribute("errorMessage", "ログインが必要です。");
//			return "login"; // ログイン画面へ遷移
//		}
//
//		Integer userId = loginUser.getId();
//
//		// 2. ユーザー情報および現在の保有ポイントを取得
//		User currentUser = userMapper.findById(userId);
//
//		// 3. ポイント履歴の一部（最新の履歴など）を取得 [cite: 20]
//		List<PointHistory> latestHistory = pointHistoryMapper.findLatestByUserId(userId);
//
//		// 4. DTOは作らず、Modelにそのまま必要な情報を格納してHTMLに渡す [cite: 4, 37]
//		model.addAttribute("user", currentUser);
//		model.addAttribute("pointHistoryList", latestHistory);
//
//		return "templates/point"; // ポイントページ（HTML）を表示
//	}
//}