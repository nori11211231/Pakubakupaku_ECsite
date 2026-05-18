//package com.example.hokkaidoec.controller;
//
//import java.util.List;
//import java.util.Random;
//
//import jakarta.servlet.http.HttpSession;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//
//import com.example.hokkaidoec.entity.GamePlayHistory;
//import com.example.hokkaidoec.entity.PointHistory;
//import com.example.hokkaidoec.entity.User;
//import com.example.hokkaidoec.form.GameForm;
//import com.example.hokkaidoec.mapper.GamePlayHistoryMapper;
//import com.example.hokkaidoec.mapper.PointHistoryMapper;
//import com.example.hokkaidoec.mapper.UserMapper;
//
//@Controller
//public class GameController {
//
//	private final GamePlayHistoryMapper gamePlayHistoryMapper;
//	private final UserMapper userMapper;
//	private final PointHistoryMapper pointHistoryMapper;
//
//	public GameController(
//			GamePlayHistoryMapper gamePlayHistoryMapper,
//			UserMapper userMapper,
//			PointHistoryMapper pointHistoryMapper) {
//		this.gamePlayHistoryMapper = gamePlayHistoryMapper;
//		this.userMapper = userMapper;
//		this.pointHistoryMapper = pointHistoryMapper;
//	}
//
//	// ガチャ画面表示
//	@GetMapping("/game")
//	public String game(Model model, HttpSession session) {
//
//		User loginUser = (User) session.getAttribute("loginUser");
//
//		if (loginUser == null) {
//			return "redirect:/login";
//		}
//
//		model.addAttribute("gameForm", new GameForm());
//		model.addAttribute("loginUser", loginUser);
//		model.addAttribute("currentPoint", loginUser.getPoint());
//
//		return "game";
//	}
//
//	// ガチャ実行・結果表示
//	@PostMapping("/game/result")
//	public String result(GameForm gameForm, Model model, HttpSession session) {
//
//		User loginUser = (User) session.getAttribute("loginUser");
//
//		if (loginUser == null) {
//			return "redirect:/login";
//		}
//
//		Integer betPoint = gameForm.getBetPoint();
//
//		// 入力チェック
//		if (betPoint == null || betPoint <= 0) {
//			model.addAttribute("gameForm", gameForm);
//			model.addAttribute("loginUser", loginUser);
//			model.addAttribute("currentPoint", loginUser.getPoint());
//			model.addAttribute("errorMessage", "使用するポイントを正しく入力してください。");
//			return "game";
//		}
//
//		// はずれ時はベットポイントの10倍を失うため、その分のポイントが必要
//		Integer losePoint = betPoint * 10;
//
//		if (loginUser.getPoint() < losePoint) {
//			model.addAttribute("gameForm", gameForm);
//			model.addAttribute("loginUser", loginUser);
//			model.addAttribute("currentPoint", loginUser.getPoint());
//			model.addAttribute("errorMessage", "はずれ時に消費するポイントが不足しています。");
//			return "game";
//		}
//
//		Random random = new Random();
//
//		// 1〜1600の乱数
//		int lottery = random.nextInt(1600) + 1;
//
//		// 超大当たり：1600分の1
//		boolean superResult = lottery == 1;
//
//		// 通常当たり：約30%
//		// 1は超大当たりなので、2〜480を通常当たりにする
//		boolean normalResult = lottery >= 2 && lottery <= 480;
//
//		// DB保存用：当たり系ならtrue、はずれならfalse
//		boolean result = superResult || normalResult;
//
//		Integer earnedPoint;
//		Integer lostPoint;
//		Integer pointChange;
//		String resultText;
//		String effectType;
//
//		if (superResult) {
//			// 超大当たり：100倍
//			earnedPoint = betPoint * 100;
//			lostPoint = 0;
//			pointChange = earnedPoint;
//			resultText = "超大当たり";
//			effectType = "super";
//
//		} else if (normalResult) {
//			// 当たり：10倍
//			earnedPoint = betPoint * 10;
//			lostPoint = 0;
//			pointChange = earnedPoint;
//			resultText = "当たり";
//			effectType = "win";
//
//		} else {
//			// はずれ：10倍失う
//			earnedPoint = 0;
//			lostPoint = betPoint * 10;
//			pointChange = -lostPoint;
//			resultText = "はずれ";
//			effectType = "lose";
//		}
//
//		// ユーザーのポイントを更新
//		Integer newPoint = loginUser.getPoint() + pointChange;
//		loginUser.setPoint(newPoint);
//		userMapper.updatePoint(loginUser);
//
//		// ガチャ履歴を登録
//		GamePlayHistory gameResult = new GamePlayHistory();
//		gameResult.setUserId(loginUser.getId());
//		gameResult.setBetPoint(betPoint);
//		gameResult.setResult(result);
//		gameResult.setEarnedPoint(earnedPoint);
//
//		gamePlayHistoryMapper.insert(gameResult);
//
//		// ポイント履歴を登録
//		PointHistory pointHistory = new PointHistory();
//		pointHistory.setUserId(loginUser.getId());
//		pointHistory.setPointChange(pointChange);
//
//		if (superResult) {
//			pointHistory.setReason("ガチャ超大当たり");
//		} else if (normalResult) {
//			pointHistory.setReason("ガチャ当たり");
//		} else {
//			pointHistory.setReason("ガチャはずれ");
//		}
//
//		pointHistoryMapper.insert(pointHistory);
//
//		// セッションのユーザー情報も更新
//		session.setAttribute("loginUser", loginUser);
//
//		// 結果画面へ渡す値
//		model.addAttribute("gameResult", gameResult);
//		model.addAttribute("loginUser", loginUser);
//		model.addAttribute("betPoint", betPoint);
//		model.addAttribute("result", result);
//		model.addAttribute("resultText", resultText);
//		model.addAttribute("earnedPoint", earnedPoint);
//		model.addAttribute("lostPoint", lostPoint);
//		model.addAttribute("currentPoint", newPoint);
//
//		// 動画切り替え用
//		// super → tyouatari.mp4
//		// win   → atari.mp4
//		// lose  → hazure.mp4
//		model.addAttribute("effectType", effectType);
//
//		return "game-result";
//	}
//
//	// ガチャ履歴画面表示
//	@GetMapping("/game/history")
//	public String history(Model model, HttpSession session) {
//
//		User loginUser = (User) session.getAttribute("loginUser");
//
//		if (loginUser == null) {
//			return "redirect:/login";
//		}
//
//		List<GamePlayHistory> gameHistories = gamePlayHistoryMapper.findByUserId(loginUser.getId());
//
//		model.addAttribute("gameHistories", gameHistories);
//		model.addAttribute("loginUser", loginUser);
//		model.addAttribute("currentPoint", loginUser.getPoint());
//
//		return "game-history";
//	}
//}