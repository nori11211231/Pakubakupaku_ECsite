package com.example.hokkaidoec.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.hokkaidoec.entity.GamePlayHistory;
import com.example.hokkaidoec.entity.PointHistory;
import com.example.hokkaidoec.entity.User;
import com.example.hokkaidoec.form.GameForm;
import com.example.hokkaidoec.mapper.GamePlayHistoryMapper;
import com.example.hokkaidoec.mapper.PointHistoryMapper;
import com.example.hokkaidoec.mapper.UserMapper;

@Controller
public class GameController {

	private final GamePlayHistoryMapper gamePlayHistoryMapper;

	private final UserMapper userMapper;

	private final PointHistoryMapper pointHistoryMapper;

	private final Random random = new Random();

	public GameController(
			GamePlayHistoryMapper gamePlayHistoryMapper,
			UserMapper userMapper,
			PointHistoryMapper pointHistoryMapper) {

		this.gamePlayHistoryMapper = gamePlayHistoryMapper;

		this.userMapper = userMapper;

		this.pointHistoryMapper = pointHistoryMapper;
	}

	// ガチャ画面
	@GetMapping("/game")
	public String showGame(
			Model model,
			HttpSession session) {

		User loginUser = (User) session.getAttribute(
				"loginUser");

		if (loginUser == null) {

			return "redirect:/login";
		}

		model.addAttribute(
				"gameForm",
				new GameForm());

		model.addAttribute(
				"currentPoint",
				loginUser.getPoint());

		model.addAttribute(
				"user",
				loginUser);

		return "game";
	}

	// 1回ガチャ
	@PostMapping("/game/result")
	public String playGame(
			GameForm gameForm,
			Model model,
			HttpSession session) {

		User loginUser = (User) session.getAttribute(
				"loginUser");

		if (loginUser == null) {

			return "redirect:/login";
		}

		Integer userId = loginUser.getId();

		Integer currentPoint = loginUser.getPoint();

		Integer betPoint = gameForm.getBetPoint();

		// 入力チェック
		if (betPoint == null
				|| betPoint <= 0) {

			model.addAttribute(
					"gameForm",
					gameForm);

			model.addAttribute(
					"currentPoint",
					currentPoint);

			model.addAttribute(
					"errorMessage",
					"ベットポイントは1以上で入力してください。");

			return "game";
		}

		// ポイント不足
		if (currentPoint < betPoint) {

			model.addAttribute(
					"gameForm",
					gameForm);

			model.addAttribute(
					"currentPoint",
					currentPoint);

			model.addAttribute(
					"errorMessage",
					"ポイント不足です。");

			return "game";
		}

		List<GameResultView> results = new ArrayList<>();

		GameResultView result = drawGacha(betPoint);

		result.setIndex(1);

		results.add(result);

		// ポイント更新
		currentPoint = currentPoint
				+ result.getPointChange();

		loginUser.setPoint(
				currentPoint);

		// DB更新
		userMapper.updatePoint(
				userId,
				currentPoint);

		// session更新
		session.setAttribute(
				"loginUser",
				loginUser);

		// 履歴保存
		GamePlayHistory history = new GamePlayHistory();

		history.setUserId(userId);

		history.setBetPoint(
				betPoint);

		history.setResult(
				result.isWin());

		history.setEarnedPoint(
				result.getEarnedPoint());

		history.setPlayedAt(
				LocalDateTime.now());

		gamePlayHistoryMapper.insert(
				history);
		PointHistory pointHistory = new PointHistory();
		pointHistory.setUserId(userId);
		pointHistory.setPointChange(result.getPointChange()); // 当たりならプラス、はずれならマイナスの値
		pointHistory.setReason("ポイントガチャ");
		pointHistory.setCreatedAt(LocalDateTime.now());

		pointHistoryMapper.insert(pointHistory);

		model.addAttribute(
				"isTenRoll",
				false);

		model.addAttribute(
				"results",
				results);

		model.addAttribute(
				"resultText",
				result.getResultText());

		model.addAttribute(
				"effectType",
				result.getEffectType());

		model.addAttribute(
				"betPoint",
				betPoint);

		model.addAttribute(
				"earnedPoint",
				result.getEarnedPoint());

		model.addAttribute(
				"lostPoint",
				result.getLostPoint());

		model.addAttribute(
				"pointChange",
				result.getPointChange());

		model.addAttribute(
				"currentPoint",
				currentPoint);

		model.addAttribute(
				"videoPath",
				result.getVideoPath());

		model.addAttribute(
				"message",
				result.getMessage());

		model.addAttribute(
				"user",
				loginUser);

		return "game-result";
	}

	// 10連ガチャ
	@PostMapping("/game/result/ten")
	public String playTenGame(
			GameForm gameForm,
			Model model,
			HttpSession session) {

		User loginUser = (User) session.getAttribute(
				"loginUser");

		if (loginUser == null) {

			return "redirect:/login";
		}

		Integer userId = loginUser.getId();

		Integer currentPoint = loginUser.getPoint();

		Integer betPoint = gameForm.getBetPoint();

		// 入力チェック
		if (betPoint == null
				|| betPoint <= 0) {

			model.addAttribute(
					"gameForm",
					gameForm);

			model.addAttribute(
					"currentPoint",
					currentPoint);

			model.addAttribute(
					"errorMessage",
					"ベットポイントは1以上で入力してください。");

			return "game";
		}

		Integer maxLostPoint = betPoint * 10;

		// ポイント不足
		if (currentPoint < maxLostPoint) {

			model.addAttribute(
					"gameForm",
					gameForm);

			model.addAttribute(
					"currentPoint",
					currentPoint);

			model.addAttribute(
					"errorMessage",
					"ポイント不足です。");

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

			if ("super".equals(
					result.getEffectType())) {

				hasSuper = true;
			}

			if (result.isWin()) {

				hasWin = true;
			}

			// 履歴保存
			GamePlayHistory history = new GamePlayHistory();

			history.setUserId(userId);

			history.setBetPoint(
					betPoint);

			history.setResult(
					result.isWin());

			history.setEarnedPoint(
					result.getEarnedPoint());

			history.setPlayedAt(
					LocalDateTime.now());

			gamePlayHistoryMapper.insert(
					history);
		}

		// ポイント更新
		currentPoint = currentPoint
				+ totalPointChange;

		loginUser.setPoint(
				currentPoint);

		// DB更新
		userMapper.updatePoint(
				userId,
				currentPoint);

		// session更新
		session.setAttribute(
				"loginUser",
				loginUser);

		PointHistory pointHistory = new PointHistory();
		pointHistory.setUserId(userId);
		pointHistory.setPointChange(totalPointChange); // 10回分のトータルの増減値
		pointHistory.setReason("ポイントガチャ（10連）");
		pointHistory.setCreatedAt(LocalDateTime.now());

		pointHistoryMapper.insert(pointHistory);

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

		model.addAttribute(
				"isTenRoll",
				true);

		model.addAttribute(
				"results",
				results);

		model.addAttribute(
				"resultText",
				summaryText);

		model.addAttribute(
				"effectType",
				summaryEffectType);

		model.addAttribute(
				"betPoint",
				betPoint);

		model.addAttribute(
				"earnedPoint",
				totalEarnedPoint);

		model.addAttribute(
				"lostPoint",
				totalLostPoint);

		model.addAttribute(
				"pointChange",
				totalPointChange);

		model.addAttribute(
				"currentPoint",
				currentPoint);

		model.addAttribute(
				"message",
				"10回分の結果です");

		model.addAttribute(
				"user",
				loginUser);

		if (!results.isEmpty()) {

			model.addAttribute(
					"videoPath",
					results.get(0)
							.getVideoPath());
		}

		return "game-result";
	}

	// ガチャ履歴
	@GetMapping("/game/history")
	public String showGameHistory(
			Model model,
			HttpSession session) {

		User loginUser = (User) session.getAttribute(
				"loginUser");

		if (loginUser == null) {

			return "redirect:/login";
		}

		Integer userId = loginUser.getId();

		List<GamePlayHistory> gameHistories =

				gamePlayHistoryMapper
						.findByUserId(userId);

		model.addAttribute(
				"gameHistories",
				gameHistories);

		model.addAttribute(
				"currentPoint",
				loginUser.getPoint());

		model.addAttribute(
				"user",
				loginUser);

		return "game-history";
	}

	// 抽選処理
	private GameResultView drawGacha(
			Integer betPoint) {

		int roll = random.nextInt(1600) + 1;

		GameResultView result = new GameResultView();

		result.setBetPoint(
				betPoint);

		// 超大当たり
		if (roll == 1) {

			result.setWin(true);

			result.setResultText(
					"超大当たり");

			result.setEffectType(
					"super");

			result.setEarnedPoint(
					betPoint * 100);

			result.setLostPoint(0);

			result.setPointChange(
					result.getEarnedPoint());

			result.setVideoPath(
					"/video/game/tyouatari.mp4");

			result.setMessage(
					"超大当たり！！");

		}
		// 当たり
		else if (roll <= 480) {

			result.setWin(true);

			result.setResultText(
					"当たり");

			result.setEffectType(
					"win");

			result.setEarnedPoint(
					betPoint * 10);

			result.setLostPoint(0);

			result.setPointChange(
					result.getEarnedPoint());

			result.setVideoPath(
					"/video/game/atari.mp4");

			result.setMessage(
					"当たり！");

		}
		// はずれ
		else {

			result.setWin(false);

			result.setResultText(
					"はずれ");

			result.setEffectType(
					"lose");

			result.setEarnedPoint(0);

			result.setLostPoint(
					betPoint);

			result.setPointChange(
					-betPoint * 10);

			result.setVideoPath(
					"/video/game/hazure.mp4");

			result.setMessage(
					"はずれ……");
		}

		return result;
	}

	// 結果表示用クラス
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