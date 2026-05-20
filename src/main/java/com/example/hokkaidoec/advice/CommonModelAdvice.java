package com.example.hokkaidoec.advice;

import java.lang.reflect.Method;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.hokkaidoec.entity.AiGrowth;
import com.example.hokkaidoec.mapper.AiGrowthMapper;
import com.example.hokkaidoec.service.AiService;

@ControllerAdvice
public class CommonModelAdvice {

	private final AiGrowthMapper aiGrowthMapper;
	private final AiService aiService;

	public CommonModelAdvice(AiGrowthMapper aiGrowthMapper, AiService aiService) {
		this.aiGrowthMapper = aiGrowthMapper;
		this.aiService = aiService;
	}

	@ModelAttribute
	public void addCommonAiModel(Model model, HttpServletRequest request, HttpSession session) {
		AiGrowth aiGrowth = getAiGrowth(session);

		String currentPath = request.getRequestURI();
		String aiWidgetMessage = aiService.createPageMessage(currentPath, aiGrowth);

		model.addAttribute("aiGrowth", aiGrowth);
		model.addAttribute("aiName", aiGrowth.getName());
		model.addAttribute("aiPersonality", aiGrowth.getPersonality());
		model.addAttribute("aiCharaImageUrl", aiService.resolveCharaImageUrl(aiGrowth));
		model.addAttribute("aiWidgetMessage", aiWidgetMessage);
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
}