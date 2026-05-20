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

		String aiReply = aiService.createChatReply(aiChatForm.getUserMessage(), aiGrowth);
		aiChatForm.setAiReply(aiReply);

		addAiModel(model, session, aiChatForm, aiGrowth);

		return "ai";
	}

	private void addAiModel(Model model, HttpSession session, AiChatForm aiChatForm, AiGrowth aiGrowth) {
		model.addAttribute("aiChatForm", aiChatForm);
		model.addAttribute("aiGrowth", aiGrowth);
		model.addAttribute("aiName", aiGrowth.getName());
		model.addAttribute("aiCharaImageUrl", aiService.resolveCharaImageUrl(aiGrowth));

		addAiStatusModel(model, session);
	}

	private void addAiStatusModel(Model model, HttpSession session) {
		Integer userId = getLoginUserId(session);

		model.addAttribute("aiLevel", "-");
		model.addAttribute("aiRankName", "ゲスト");
		model.addAttribute("aiExpText", "0");
		model.addAttribute("nextRankMessage", "ログインすると経験値と次のランクが表示されます。");

		if (userId == null) {
			return;
		}

		Map<String, Object> aiStatus = aiGrowthMapper.findAiStatusByUserId(userId);

		if (aiStatus == null) {
			model.addAttribute("nextRankMessage", "ランク情報が見つかりません。");
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

		model.addAttribute("aiLevel", rankId);
		model.addAttribute("aiRankName", rankName);
		model.addAttribute("aiExpText", formatNumber(totalPurchaseAmount));

		if (nextRankMinAmount == null) {
			model.addAttribute("nextRankMessage", "最高ランクです！");
		} else {
			int remainingExp = nextRankMinAmount - totalPurchaseAmount;

			if (remainingExp < 0) {
				remainingExp = 0;
			}

			model.addAttribute(
					"nextRankMessage",
					"次のランク「" + nextRankName + "」まであと " + formatNumber(remainingExp) + " 経験値");
		}
	}

	private AiGrowth getAiGrowth(HttpSession session) {
		Integer userId = getLoginUserId(session);

		if (userId == null) {
			return aiService.createDefaultAiGrowth();
		}

		AiGrowth aiGrowth = aiGrowthMapper.findByUserId(userId);

		if (aiGrowth == null) {
			return aiService.createDefaultAiGrowth();
		}

		return aiGrowth;
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