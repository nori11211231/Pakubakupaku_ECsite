/*package com.example.hokkaidoec.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.hokkaidoec.entity.PointHistory;
import com.example.hokkaidoec.entity.User;

@Controller
@RequestMapping("/points") // ポイント関連のパスを指定
public class PointController {

	// 責任者ルールに基づき、既存のMapperをインジェクションして利用
	private final UserMapper userMapper;
	private final PointHistoryMapper pointHistoryMapper;

	// コンストラクタインジェクション
	public PointController(UserMapper userMapper, PointHistoryMapper pointHistoryMapper) {
		this.userMapper = userMapper;
		this.pointHistoryMapper = pointHistoryMapper;
	}

	*//**
		* ポイントページの表示
		*//*
			@GetMapping
			public String showPointPage(HttpSession session, Model model) {
			
			// 1. セッション等からログイン中のユーザーIDを取得（※実装に合わせて調整してください）
			// 例：ログイン時にセッションにセットされたUserオブジェクト、またはIDから取得
			User loginUser = (User) session.getAttribute("loginUser");
			
			// 未ログイン時のエラーハンドリング（最終チェックリストの条件）
			if (loginUser == null) {
				model.addAttribute("errorMessage", "ログインが必要です。");
				return "login"; // ログイン画面へ遷移
			}
			
			Integer userId = loginUser.getId();
			
			// 2. ユーザー情報および現在の保有ポイントを取得
			// UserMapperのfindByIdを使用して、最新のDB情報を取得（pointやtotal_purchase_amount含む）
			User currentUser = userMapper.findById(userId);
			
			// 3. ポイント履歴の一部（最新の履歴など）を取得
			// PointHistoryMapperのメソッドを活用
			List<PointHistory> latestHistory = pointHistoryMapper.findLatestByUserId(userId);
			// もし全履歴を表示する場合は、方針に合わせて findByUserId(userId) を使用
			// List<PointHistory> allHistory = pointHistoryMapper.findByUserId(userId);
			
			// 4. DTOは作らず、Modelにそのまま必要な情報を格納してHTMLに渡す
			model.addAttribute("user", currentUser); // ユーザー情報・保有ポイント(currentUser.getPoint())
			model.addAttribute("pointHistoryList", latestHistory); // ポイント履歴の一部
			
			return "templates/point"; // ポイントページ（HTML）を表示
			}
			}*/