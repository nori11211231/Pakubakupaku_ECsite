package com.example.hokkaidoec.controller;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.hokkaidoec.entity.Rank;
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

	// 💡 ここに @PostMapping("/register") を復活させます！
	@PostMapping("/register")
	public String registerUser(
			@Validated @ModelAttribute("form") UserForm form,
			BindingResult bindingResult,
			Model model) {

		// 1. バリデーションエラーのチェック
		if (bindingResult.hasErrors()) {
			return "register";
		}

		try {
			// 2. 実際の登録処理を実行
			userService.register(form);
			return "redirect:/login";

		} catch (IllegalArgumentException e) {
			// 3. サービス層（DB）の例外キャッチ
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

		String rankName = "通常ポイント";

		// 🌟【修正】ログインユーザーがランクIDを持っているかチェック
		if (loginUser.getRankId() != null) {
			// userService（中身はRankMapper）を使って、DBから該当するランクの「実体（インスタンス）」を取得する
			Rank currentRank = userService.findRankById(loginUser.getRankId());

			// DBから無事にデータが取れたら、そのオブジェクトから名前を取り出す
			if (currentRank != null) {
				rankName = currentRank.getRankName(); // 💡 クラス名ではなく、取得した「currentRank」から呼び出します！
			}
		}

		Map<String, Object> pointData = new HashMap<>();
		pointData.put("name", rankName); // HTMLの ${point.name} で表示される
		model.addAttribute("point", pointData);

		// 3. AIのダミーデータ（Map）
		Map<String, Object> aiData = new HashMap<>();

		// 🌟【ここを修正！】ダミーの「3」ではなく、ユーザーの rankId をレベルとしてセット
		Integer level = loginUser.getRankId();
		if (level == null) {
			level = 1; // もしrankIdが設定されていなければ初期値として1にする（安全処理）
		}
		aiData.put("level", level);

		// 🌟【ここを修正！】ユーザーの総購入金額をAIの経験値（exp）としてセットする
		// ※ もし初期状態などでnullになる可能性がある場合は、0を代入する安全処理を入れると安心です
		Integer exp = loginUser.getTotalPurchaseAmount();
		if (exp == null) {
			exp = 0; // 金額がまだ無い（null）なら0にする
		}
		aiData.put("exp", exp);

		model.addAttribute("ai", aiData);

		// 4. 注文履歴のダミーデータ（必要であれば追加）
		// 前回のList<Map>をここに置いておくと、注文履歴もエラーにならず表示されます！★★★★

		return "mypage";
	}

	@GetMapping("/mypage/minigame-history")
	public String showMinigameHistory(Model model, HttpSession session) {
		// 1. セッションからログインユーザーを取得
		User loginUser = (User) session.getAttribute("loginUser");

		// セッションが空（未ログイン）ならログイン画面へリダイレクト
		if (loginUser == null) {
			return "redirect:/login";
		}

		// 2. 画面表示に必要なユーザー情報をModelにセット
		model.addAttribute("user", loginUser);

		// 3. 【タスク】ミニゲーム履歴のデータ取得
		// 今はまだダミー、もしくはuserService経由でDBからリストを取得する処理をここに書きます
		// 例: List<MinigameHistory> historyList = userService.getMinigameHistoryByUserId(loginUser.getId());
		// model.addAttribute("historyList", historyList);

		// 4. 表示するHTML（Thymeleafテンプレート）の名前を返す
		// src/main/resources/templates/minigame-history.html を読み込みます
		return "minigame-history";
	}
}
