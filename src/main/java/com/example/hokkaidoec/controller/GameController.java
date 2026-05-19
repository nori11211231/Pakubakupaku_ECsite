package com.example.hokkaidoec.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jakarta.servlet.http.HttpSession;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.hokkaidoec.entity.GamePlayHistory;
import com.example.hokkaidoec.form.GameForm;
import com.example.hokkaidoec.mapper.GamePlayHistoryMapper;

@Controller
public class GameController {

	private final GamePlayHistoryMapper gamePlayHistoryMapper;
	private final JdbcTemplate jdbcTemplate;
	private final Random random = new Random();

	public GameController(GamePlayHistoryMapper gamePlayHistoryMapper, JdbcTemplate jdbcTemplate) {
		this.gamePlayHistoryMapper = gamePlayHistoryMapper;
		this.jdbcTemplate = jdbcTemplate;
	}

	@GetMapping("/")
	public String showTop() {
		return "top";
	}

	@GetMapping("/game")
	public String showGame(Model model, HttpSession session) {

		Integer userId = getLoginUserId(session);
		if (userId == null) {
			return "redirect:/login";
		}

		Integer currentPoint = getCurrentPoint(userId);

		model.addAttribute("gameForm", new GameForm());
		model.addAttribute("currentPoint", currentPoint);

		return "game";
	}

	@PostMapping("/game/result")
	public String playGame(GameForm gameForm, Model model, HttpSession session) {

		Integer userId = getLoginUserId(session);
		if (userId == null) {
			return "redirect:/login";
		}

		Integer currentPoint = getCurrentPoint(userId);
		Integer betPoint = gameForm.getBetPoint();

		if (betPoint == null || betPoint <= 0) {
			model.addAttribute("gameForm", gameForm);
			model.addAttribute("currentPoint", currentPoint);
			model.addAttribute("errorMessage", "ベットポイントは1以上で入力してください。");
			return "game";
		}

		if (currentPoint < betPoint) {
			model.addAttribute("gameForm", gameForm);
			model.addAttribute("currentPoint", currentPoint);
			model.addAttribute("errorMessage", "ポイントが不足しています。");
			return "game";
		}

		List<GameResultView> results = new ArrayList<>();

		GameResultView result = drawGacha(betPoint);
		result.setIndex(1);
		results.add(result);

		GamePlayHistory history = new GamePlayHistory();
		history.setUserId(userId);
		history.setBetPoint(betPoint);
		history.setResult(result.isWin());
		history.setEarnedPoint(result.getEarnedPoint());
		history.setPlayedAt(LocalDateTime.now());

		gamePlayHistoryMapper.insert(history);

		Integer newPoint = currentPoint + result.getPointChange();
		updateCurrentPoint(userId, newPoint);

		model.addAttribute("isTenRoll", false);
		model.addAttribute("results", results);
		model.addAttribute("resultText", result.getResultText());
		model.addAttribute("effectType", result.getEffectType());
		model.addAttribute("betPoint", betPoint);
		model.addAttribute("earnedPoint", result.getEarnedPoint());
		model.addAttribute("lostPoint", result.getLostPoint());
		model.addAttribute("pointChange", result.getPointChange());
		model.addAttribute("currentPoint", newPoint);
		model.addAttribute("videoPath", result.getVideoPath());
		model.addAttribute("message", result.getMessage());

		return "game-result";
	}

	@PostMapping("/game/result/ten")
	public String playTenGame(GameForm gameForm, Model model, HttpSession session) {

		Integer userId = getLoginUserId(session);
		if (userId == null) {
			return "redirect:/login";
		}

		Integer currentPoint = getCurrentPoint(userId);
		Integer betPoint = gameForm.getBetPoint();

		if (betPoint == null || betPoint <= 0) {
			model.addAttribute("gameForm", gameForm);
			model.addAttribute("currentPoint", currentPoint);
			model.addAttribute("errorMessage", "ベットポイントは1以上で入力してください。");
			return "game";
		}

		Integer maxLostPoint = betPoint * 10;

		if (currentPoint < maxLostPoint) {
			model.addAttribute("gameForm", gameForm);
			model.addAttribute("currentPoint", currentPoint);
			model.addAttribute("errorMessage", "10連を引くには、ベットポイント×10回分のポイントが必要です。");
			return "game";
		}

		List<GameResultView> results = new ArrayList<>();

		int totalEarnedPoint = 0;
		int totalLostPoint = 0;
		int totalPointChange = 0;

		boolean hasSuper = false;
		boolean hasWin = false;

		for (int i = 1; i <= 10; i++) {
			GameResultView result = drawGacha(betPoint);
			result.setIndex(i);
			results.add(result);

			totalEarnedPoint += result.getEarnedPoint();
			totalLostPoint += result.getLostPoint();
			totalPointChange += result.getPointChange();

			if ("super".equals(result.getEffectType())) {
				hasSuper = true;
			}

			if (result.isWin()) {
				hasWin = true;
			}

			GamePlayHistory history = new GamePlayHistory();
			history.setUserId(userId);
			history.setBetPoint(betPoint);
			history.setResult(result.isWin());
			history.setEarnedPoint(result.getEarnedPoint());
			history.setPlayedAt(LocalDateTime.now());

			gamePlayHistoryMapper.insert(history);
		}

		String summaryText;
		String summaryEffectType;

		if (hasSuper) {
			summaryText = "10連結果：超大当たりあり！";
			summaryEffectType = "super";
		} else if (hasWin) {
			summaryText = "10連結果：当たりあり！";
			summaryEffectType = "win";
		} else {
			summaryText = "10連結果：全敗……";
			summaryEffectType = "lose";
		}

		Integer newPoint = currentPoint + totalPointChange;
		updateCurrentPoint(userId, newPoint);

		model.addAttribute("isTenRoll", true);
		model.addAttribute("results", results);
		model.addAttribute("resultText", summaryText);
		model.addAttribute("effectType", summaryEffectType);
		model.addAttribute("betPoint", betPoint);
		model.addAttribute("earnedPoint", totalEarnedPoint);
		model.addAttribute("lostPoint", totalLostPoint);
		model.addAttribute("pointChange", totalPointChange);
		model.addAttribute("currentPoint", newPoint);
		model.addAttribute("message", "10回分の結果をまとめて表示します。");

		if (!results.isEmpty()) {
			model.addAttribute("videoPath", results.get(0).getVideoPath());
		}

		return "game-result";
	}

	@GetMapping("/game/history")
	public String showGameHistory(Model model, HttpSession session) {

		Integer userId = getLoginUserId(session);
		if (userId == null) {
			return "redirect:/login";
		}

		Integer currentPoint = getCurrentPoint(userId);

		List<GamePlayHistory> gameHistories = gamePlayHistoryMapper.findByUserId(userId);

		model.addAttribute("gameHistories", gameHistories);
		model.addAttribute("currentPoint", currentPoint);

		return "game-history";
	}

	private Integer getLoginUserId(HttpSession session) {
		Object loginUserId = session.getAttribute("userId");

		if (loginUserId == null) {
			return null;
		}

		return (Integer) loginUserId;
	}

	private Integer getCurrentPoint(Integer userId) {
		return jdbcTemplate.queryForObject(
				"SELECT point FROM users WHERE id = ?",
				Integer.class,
				userId);
	}

	private void updateCurrentPoint(Integer userId, Integer newPoint) {
		jdbcTemplate.update(
				"UPDATE users SET point = ? WHERE id = ?",
				newPoint,
				userId);
	}

	private GameResultView drawGacha(Integer betPoint) {

		int roll = random.nextInt(1600) + 1;

		GameResultView result = new GameResultView();
		result.setBetPoint(betPoint);

		if (roll == 1) {
			result.setWin(true);
			result.setResultText("超大当たり");
			result.setEffectType("super");
			result.setEarnedPoint(betPoint * 100);
			result.setLostPoint(0);
			result.setPointChange(result.getEarnedPoint());
			result.setVideoPath("/video/game/tyouatari.mp4");
			result.setMessage("超大当たり！！奇跡の1600分の1！ベットポイントの100倍を獲得！");

		} else if (roll <= 480) {
			result.setWin(true);
			result.setResultText("当たり");
			result.setEffectType("win");
			result.setEarnedPoint(betPoint * 10);
			result.setLostPoint(0);
			result.setPointChange(result.getEarnedPoint());
			result.setVideoPath("/video/game/atari.mp4");
			result.setMessage("当たり！ベットポイントの10倍を獲得！");

		} else {
			result.setWin(false);
			result.setResultText("はずれ");
			result.setEffectType("lose");
			result.setEarnedPoint(0);
			result.setLostPoint(betPoint);
			result.setPointChange(-betPoint);
			result.setVideoPath("/video/game/hazure.mp4");
			result.setMessage("はずれ……ベットポイントを失いました。");
		}

		return result;
	}

	public static class GameResultView {

		private Integer index;
		private Integer betPoint;
		private boolean win;
		private String resultText;
		private String effectType;
		private Integer earnedPoint;
		private Integer lostPoint;
		private Integer pointChange;
		private String videoPath;
		private String message;

		public Integer getIndex() {
			return index;
		}

		public void setIndex(Integer index) {
			this.index = index;
		}

		public Integer getBetPoint() {
			return betPoint;
		}

		public void setBetPoint(Integer betPoint) {
			this.betPoint = betPoint;
		}

		public boolean isWin() {
			return win;
		}

		public void setWin(boolean win) {
			this.win = win;
		}

		public String getResultText() {
			return resultText;
		}

		public void setResultText(String resultText) {
			this.resultText = resultText;
		}

		public String getEffectType() {
			return effectType;
		}

		public void setEffectType(String effectType) {
			this.effectType = effectType;
		}

		public Integer getEarnedPoint() {
			return earnedPoint;
		}

		public void setEarnedPoint(Integer earnedPoint) {
			this.earnedPoint = earnedPoint;
		}

		public Integer getLostPoint() {
			return lostPoint;
		}

		public void setLostPoint(Integer lostPoint) {
			this.lostPoint = lostPoint;
		}

		public Integer getPointChange() {
			return pointChange;
		}

		public void setPointChange(Integer pointChange) {
			this.pointChange = pointChange;
		}

		public String getVideoPath() {
			return videoPath;
		}

		public void setVideoPath(String videoPath) {
			this.videoPath = videoPath;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}
}