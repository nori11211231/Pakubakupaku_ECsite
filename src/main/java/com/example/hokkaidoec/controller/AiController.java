package com.example.hokkaidoec.controller;

import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.hokkaidoec.entity.AiGrowth;
import com.example.hokkaidoec.form.AiChatForm;
import com.example.hokkaidoec.mapper.AiGrowthMapper;
import com.example.hokkaidoec.service.AiService;

@Controller
public class AiController {

	private final AiGrowthMapper aiGrowthMapper;
	private final AiService aiService;

	public AiController(AiGrowthMapper aiGrowthMapper, AiService aiService) {
		this.aiGrowthMapper = aiGrowthMapper;
		this.aiService = aiService;
	}

	@GetMapping("/ai")
	public String showAiPage(Model model, HttpSession session) {
		AiChatForm aiChatForm = new AiChatForm();
		AiGrowth aiGrowth = getAiGrowth(session);

		addAiModel(model, session, aiChatForm, aiGrowth);

		return "ai";
	}

	@PostMapping("/ai/chat")
	public String chat(AiChatForm aiChatForm, Model model, HttpSession session) {
		AiGrowth aiGrowth = getAiGrowth(session);

		String aiReply = aiService.createChatReply(
				aiGrowth,
				aiChatForm.getUserMessage(),
				"AIページ");

		aiChatForm.setAiReply(aiReply);

		addAiModel(model, session, aiChatForm, aiGrowth);

		return "ai";
	}

	@PostMapping("/ai/chat/reply")
	@ResponseBody
	public Map<String, String> chatReply(
			@RequestParam(value = "userMessage", required = false) String userMessage,
			HttpSession session) {

		AiGrowth aiGrowth = getAiGrowth(session);

		// ★ Ajaxでもセッションを最新状態にする
		updateAiSession(session, aiGrowth);

		String aiReply = aiService.createChatReply(
				aiGrowth,
				userMessage,
				"AIページ。ユーザーがAIコンシェルジュに自由質問をしている。");

		return Map.of("reply", aiReply);
	}

	@PostMapping("/ai/widget/reply")
	@ResponseBody
	public Map<String, String> widgetReply(
			@RequestParam(value = "userMessage", required = false) String userMessage,
			@RequestParam(value = "pageContext", required = false) String pageContext,
			HttpSession session) {

		AiGrowth aiGrowth = getAiGrowth(session);

		// ★ ウィジェットでもセッションを最新状態にする
		updateAiSession(session, aiGrowth);

		String context = "全画面共通AIウィジェット。";

		if (pageContext != null && !pageContext.isBlank()) {
			context += pageContext;
		}

		String aiReply = aiService.createChatReply(
				aiGrowth,
				userMessage,
				context);

		return Map.of("reply", aiReply);
	}

	@PostMapping("/ai/character/change")
	public String changeCharacter(
			@RequestParam("charaKey") String charaKey,
			HttpSession session) {

		Integer userId = getLoginUserId(session);

		if (userId == null) {
			return "redirect:/login";
		}

		Integer rankId = getLoginUserRankId(userId);

		if (rankId == null) {
			rankId = 1;
		}

		aiService.changeRegionCharacter(userId, rankId, charaKey);

		// ★ 変更直後にDBから取り直してセッションへ保存する
		AiGrowth updatedAiGrowth = aiService.getOrCreateAiGrowthByRank(userId, rankId);
		updateAiSession(session, updatedAiGrowth);

		return "redirect:/ai";
	}

	private void addAiModel(Model model, HttpSession session, AiChatForm aiChatForm, AiGrowth aiGrowth) {
		Integer userId = getLoginUserId(session);
		Integer rankId = 1;

		if (userId != null) {
			Integer foundRankId = getLoginUserRankId(userId);

			if (foundRankId != null) {
				rankId = foundRankId;
			}
		}

		String aiCharaImageUrl = aiService.resolveCharaImageUrl(aiGrowth);
		boolean canChangeAiCharacter = aiService.canChangeRegionCharacter(rankId);
		String currentAiCharacterKey = aiService.getCurrentRegionCharacterKey(aiGrowth);

		model.addAttribute("aiChatForm", aiChatForm);
		model.addAttribute("aiGrowth", aiGrowth);
		model.addAttribute("aiName", aiGrowth.getName());
		model.addAttribute("aiCharaImageUrl", aiCharaImageUrl);

		model.addAttribute("aiRegionCharacters", aiService.getRegionCharacterOptions());
		model.addAttribute("canChangeAiCharacter", canChangeAiCharacter);
		model.addAttribute("currentAiCharacterKey", currentAiCharacterKey);

		// ★ AI表示情報をセッションにも入れる
		session.setAttribute("aiGrowth", aiGrowth);
		session.setAttribute("aiName", aiGrowth.getName());
		session.setAttribute("aiPersonality", aiGrowth.getPersonality());
		session.setAttribute("aiCharaImageUrl", aiCharaImageUrl);
		session.setAttribute("aiRankId", rankId);
		session.setAttribute("canChangeAiCharacter", canChangeAiCharacter);
		session.setAttribute("currentAiCharacterKey", currentAiCharacterKey);

		addAiStatusModel(model, session);
	}

	private void addAiStatusModel(Model model, HttpSession session) {
		Integer userId = getLoginUserId(session);

		model.addAttribute("aiLevel", "-");
		model.addAttribute("aiRankName", "ゲスト");
		model.addAttribute("aiExpText", "0");
		model.addAttribute("nextRankMessage", "ログインすると経験値と次のランクが表示されます。");

		session.setAttribute("aiLevel", "-");
		session.setAttribute("aiRankName", "ゲスト");
		session.setAttribute("aiExpText", "0");
		session.setAttribute("nextRankMessage", "ログインすると経験値と次のランクが表示されます。");

		if (userId == null) {
			return;
		}

		Map<String, Object> aiStatus = aiGrowthMapper.findAiStatusByUserId(userId);

		if (aiStatus == null) {
			model.addAttribute("nextRankMessage", "ランク情報が見つかりません。");
			session.setAttribute("nextRankMessage", "ランク情報が見つかりません。");
			return;
		}

		Integer rankId = toInteger(getMapValue(aiStatus, "rankId"));
		Integer totalPurchaseAmount = toInteger(getMapValue(aiStatus, "totalPurchaseAmount"));
		String rankName = toStringValue(getMapValue(aiStatus, "rankName"), "ランク未設定");
		String nextRankName = toStringValue(getMapValue(aiStatus, "nextRankName"), null);
		Integer nextRankMinAmount = toInteger(getMapValue(aiStatus, "nextRankMinAmount"));

		if (rankId == null) {
			rankId = 0;
		}

		if (totalPurchaseAmount == null) {
			totalPurchaseAmount = 0;
		}

		String aiLevel = String.valueOf(rankId);
		String aiExpText = formatNumber(totalPurchaseAmount);

		model.addAttribute("aiLevel", aiLevel);
		model.addAttribute("aiRankName", rankName);
		model.addAttribute("aiExpText", aiExpText);

		session.setAttribute("aiLevel", aiLevel);
		session.setAttribute("aiRankName", rankName);
		session.setAttribute("aiExpText", aiExpText);

		String nextRankMessage;

		if (nextRankMinAmount == null) {
			nextRankMessage = "最高ランクです！";
		} else {
			int remainingExp = nextRankMinAmount - totalPurchaseAmount;

			if (remainingExp < 0) {
				remainingExp = 0;
			}

			nextRankMessage = "次のランク「" + nextRankName + "」まであと "
					+ formatNumber(remainingExp) + " 経験値";
		}

		model.addAttribute("nextRankMessage", nextRankMessage);
		session.setAttribute("nextRankMessage", nextRankMessage);
	}

	private void updateAiSession(HttpSession session, AiGrowth aiGrowth) {
		if (aiGrowth == null) {
			return;
		}

		Integer userId = getLoginUserId(session);
		Integer rankId = 1;

		if (userId != null) {
			Integer foundRankId = getLoginUserRankId(userId);

			if (foundRankId != null) {
				rankId = foundRankId;
			}
		}

		String aiCharaImageUrl = aiService.resolveCharaImageUrl(aiGrowth);

		session.setAttribute("aiGrowth", aiGrowth);
		session.setAttribute("aiName", aiGrowth.getName());
		session.setAttribute("aiPersonality", aiGrowth.getPersonality());
		session.setAttribute("aiCharaImageUrl", aiCharaImageUrl);
		session.setAttribute("aiRankId", rankId);
		session.setAttribute("canChangeAiCharacter", aiService.canChangeRegionCharacter(rankId));
		session.setAttribute("currentAiCharacterKey", aiService.getCurrentRegionCharacterKey(aiGrowth));
	}

	private AiGrowth getAiGrowth(HttpSession session) {
		Integer userId = getLoginUserId(session);

		if (userId == null) {
			AiGrowth defaultAiGrowth = aiService.createDefaultAiGrowth();
			updateAiSession(session, defaultAiGrowth);
			return defaultAiGrowth;
		}

		Integer rankId = getLoginUserRankId(userId);

		if (rankId == null) {
			rankId = 1;
		}

		AiGrowth aiGrowth = aiService.getOrCreateAiGrowthByRank(userId, rankId);
		updateAiSession(session, aiGrowth);

		return aiGrowth;
	}

	private Integer getLoginUserRankId(Integer userId) {
		if (userId == null) {
			return null;
		}

		Map<String, Object> aiStatus = aiGrowthMapper.findAiStatusByUserId(userId);

		if (aiStatus == null) {
			return null;
		}

		return toInteger(getMapValue(aiStatus, "rankId"));
	}

	private Integer getLoginUserId(HttpSession session) {
		Object userId = session.getAttribute("userId");

		if (userId instanceof Integer) {
			return (Integer) userId;
		}

		if (userId instanceof Number) {
			return ((Number) userId).intValue();
		}

		Object loginUserId = session.getAttribute("loginUserId");

		if (loginUserId instanceof Integer) {
			return (Integer) loginUserId;
		}

		if (loginUserId instanceof Number) {
			return ((Number) loginUserId).intValue();
		}

		Object loginUser = session.getAttribute("loginUser");

		if (loginUser != null) {
			try {
				Method getIdMethod = loginUser.getClass().getMethod("getId");
				Object id = getIdMethod.invoke(loginUser);

				if (id instanceof Integer) {
					return (Integer) id;
				}

				if (id instanceof Number) {
					return ((Number) id).intValue();
				}
			} catch (Exception e) {
				return null;
			}
		}

		return null;
	}

	private Object getMapValue(Map<String, Object> map, String key) {
		if (map.containsKey(key)) {
			return map.get(key);
		}

		for (String mapKey : map.keySet()) {
			if (mapKey.equalsIgnoreCase(key)) {
				return map.get(mapKey);
			}
		}

		return null;
	}

	private Integer toInteger(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof Integer) {
			return (Integer) value;
		}

		if (value instanceof Number) {
			return ((Number) value).intValue();
		}

		try {
			return Integer.parseInt(value.toString());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private String toStringValue(Object value, String defaultValue) {
		if (value == null) {
			return defaultValue;
		}

		return value.toString();
	}

	private String formatNumber(Integer number) {
		if (number == null) {
			return "0";
		}

		return NumberFormat.getNumberInstance(Locale.JAPAN).format(number);
	}

}