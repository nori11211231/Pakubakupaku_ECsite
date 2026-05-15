package com.example.hokkaidoec.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

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
	private final Random random = new Random();

	public GameController(GamePlayHistoryMapper gamePlayHistoryMapper) {
		this.gamePlayHistoryMapper = gamePlayHistoryMapper;
	}

	// ガチャ画面を表示
	@GetMapping("/game")
	public String showGame(Model model) {

		model.addAttribute("gameForm", new GameForm());

		// 仮の現在ポイント
		// UserMapper連携後に、usersテーブルから取得する形に変更する
		model.addAttribute("currentPoint", 1000);

		return "game";
	}

	// ガチャ実行
	@PostMapping("/game")
	public String playGame(GameForm gameForm, Model model) {

		// 仮のユーザーID
		// ログイン機能と連携後に、ログイン中のユーザーIDへ変更する
		Integer userId = 1;

		Integer betPoint = gameForm.getBetPoint();

		if (betPoint == null || betPoint <= 0) {
			model.addAttribute("gameForm", gameForm);
			model.addAttribute("currentPoint", 1000);
			model.addAttribute("errorMessage", "ベットポイントは1以上で入力してください。");
			return "game";
		}

		// 1〜15000のランダムな数字を作る
		int roll = random.nextInt(15000) + 1;

		Boolean result;
		String resultType;
		String resultText;
		Integer earnedPoint;
		String videoPath;
		String message;

		if (roll == 1) {
			// 超大当たり：15000分の1
			result = true;
			resultType = "SUPER_JACKPOT";
			resultText = "超大当たり";
			earnedPoint = betPoint * 100;
			videoPath = "/video/game/gacha-super.mp4";
			message = "超大当たり！！ベットポイントの100倍を獲得！";

		} else if (roll <= 10) {
			// 大当たり
			result = true;
			resultType = "BIG_WIN";
			resultText = "大当たり";
			earnedPoint = betPoint * 10;
			videoPath = "/video/game/gacha-big-win.mp4";
			message = "大当たり！ベットポイントの10倍を獲得！";

		} else if (roll <= 3000) {
			// 当たり
			result = true;
			resultType = "WIN";
			resultText = "当たり";
			earnedPoint = betPoint * 2;
			videoPath = "/video/game/gacha-win.mp4";
			message = "当たり！ベットポイントの2倍を獲得！";

		} else {
			// はずれ
			result = false;
			resultType = "LOSE";
			resultText = "はずれ";
			earnedPoint = 0;
			videoPath = "/video/game/gacha-lose.mp4";
			message = "はずれ…獲得ポイントは0です。";
		}

		// ガチャ履歴を作成
		GamePlayHistory history = new GamePlayHistory();
		history.setUserId(userId);
		history.setBetPoint(betPoint);
		history.setResult(result);
		history.setResultType(resultType);
		history.setEarnedPoint(earnedPoint);
		history.setPlayedAt(LocalDateTime.now());

		// DBに保存
		gamePlayHistoryMapper.insert(history);

		// 画面表示用データ
		history.setResultText(resultText);
		history.setVideoPath(videoPath);
		history.setMessage(message);

		// 仮の現在ポイント
		// 後でUserMapperとPointHistoryMapperをつないだら正しい値に変更する
		history.setCurrentPoint(1000 + earnedPoint);

		model.addAttribute("gameResult", history);
		model.addAttribute("betPoint", history.getBetPoint());
		model.addAttribute("result", history.getResult());
		model.addAttribute("resultType", history.getResultType());
		model.addAttribute("resultText", history.getResultText());
		model.addAttribute("earnedPoint", history.getEarnedPoint());
		model.addAttribute("currentPoint", history.getCurrentPoint());
		model.addAttribute("videoPath", history.getVideoPath());
		model.addAttribute("message", history.getMessage());

		return "game-result";
	}

	// ガチャ履歴画面を表示
	@GetMapping("/game/history")
	public String showGameHistory(Model model) {

		// 仮のユーザーID
		Integer userId = 1;

		List<GamePlayHistory> gameHistories = gamePlayHistoryMapper.findByUserId(userId);

		model.addAttribute("gameHistories", gameHistories);

		return "game-history";
	}
}