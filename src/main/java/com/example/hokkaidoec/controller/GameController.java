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
//import com.example.pakubakupaku_ecsite.mapper.PointHistoryMapper;
//import com.example.pakubakupaku_ecsite.mapper.UserMapper;
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
//		if (betPoint == null || betPoint <= 0) {
//			model.addAttribute("gameForm", gameForm);
//			model.addAttribute("loginUser", loginUser);
//			model.addAttribute("currentPoint", loginUser.getPoint());
//			model.addAttribute("errorMessage", "使用するポイントを正しく入力してください。");
//			return "game";
//		}
//
//		if (loginUser.getPoint() < betPoint) {
//			model.addAttribute("gameForm", gameForm);
//			model.addAttribute("loginUser", loginUser);
//			model.addAttribute("currentPoint", loginUser.getPoint());
//			model.addAttribute("errorMessage", "ポイントが不足しています。");
//			return "game";
//		}
//
//		Random random = new Random();
//
//		// とりあえず 30% で当たり
//		boolean result = random.nextInt(100) < 30;
//
//		Integer earnedPoint;
//		Integer pointChange;
//		String resultText;
//		String effectType;
//
//		if (result) {
//			earnedPoint = betPoint * 3;
//			pointChange = earnedPoint;
//			resultText = "当たり";
//			effectType = "win";
//		} else {
//			earnedPoint = 0;
//			pointChange = -betPoint;
//			resultText = "はずれ";
//			effectType = "lose";
//		}
//
//		Integer newPoint = loginUser.getPoint() + pointChange;
//
//		loginUser.setPoint(newPoint);
//		userMapper.updatePoint(loginUser);
//
//		GamePlayHistory gameResult = new GamePlayHistory();
//		gameResult.setUserId(loginUser.getId());
//		gameResult.setBetPoint(betPoint);
//		gameResult.setResult(result);
//		gameResult.setEarnedPoint(earnedPoint);
//
//		gamePlayHistoryMapper.insert(gameResult);
//
//		PointHistory pointHistory = new PointHistory();
//		pointHistory.setUserId(loginUser.getId());
//		pointHistory.setPointChange(pointChange);
//
//		if (result) {
//			pointHistory.setReason("ガチャ当たり");
//		} else {
//			pointHistory.setReason("ガチャはずれ");
//		}
//
//		pointHistoryMapper.insert(pointHistory);
//
//		session.setAttribute("loginUser", loginUser);
//
//		model.addAttribute("gameResult", gameResult);
//		model.addAttribute("loginUser", loginUser);
//		model.addAttribute("betPoint", betPoint);
//		model.addAttribute("result", result);
//		model.addAttribute("resultText", resultText);
//		model.addAttribute("earnedPoint", earnedPoint);
//		model.addAttribute("lostPoint", betPoint);
//		model.addAttribute("currentPoint", newPoint);
//		model.addAttribute("effectType", effectType);
//
//		return "game-result";
//	}
//
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
//
//		return "game-history";
//	}
//}