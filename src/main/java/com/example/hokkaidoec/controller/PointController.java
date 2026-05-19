package com.example.hokkaidoec.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.hokkaidoec.entity.PointHistory;
import com.example.hokkaidoec.entity.User;
import com.example.hokkaidoec.mapper.PointHistoryMapper;
import com.example.hokkaidoec.mapper.UserMapper;

@Controller
public class PointController {

	private final UserMapper userMapper;
	private final PointHistoryMapper pointHistoryMapper;

	public PointController(UserMapper userMapper, PointHistoryMapper pointHistoryMapper) {
		this.userMapper = userMapper;
		this.pointHistoryMapper = pointHistoryMapper;
	}

	/**
	 * ポイントページの表示
	 */
	@GetMapping("/points")
	public String showPointPage(HttpSession session, Model model) {

		User loginUser = (User) session.getAttribute("loginUser");

		if (loginUser == null) {
			return "redirect:/login";
		}

		User currentUser = userMapper.findById(loginUser.getId());

		List<PointHistory> latestHistory = pointHistoryMapper.findLatestByUserEmail(loginUser.getEmail());

		model.addAttribute("loginUser", currentUser);
		model.addAttribute("user", currentUser);
		model.addAttribute("currentPoint", currentUser.getPoint());
		model.addAttribute("pointHistoryList", latestHistory);

		List<Map<String, Object>> missions = new ArrayList<>();

		Map<String, Object> adMission = new HashMap<>();
		adMission.put("missionType", "ad");
		adMission.put("missionName", "広告を見る");
		adMission.put("missionPoint", 10);
		adMission.put("buttonText", "広告を見て10ポイント獲得");
		missions.add(adMission);

		Map<String, Object> shareMission = new HashMap<>();
		shareMission.put("missionType", "share");
		shareMission.put("missionName", "Xで共有する");
		shareMission.put("missionPoint", 20);
		shareMission.put("buttonText", "Xで共有して20ポイント獲得");
		missions.add(shareMission);

		model.addAttribute("missions", missions);

		return "point";
	}

	/**
	 * 広告を見るミッション
	 */
	@PostMapping("/points/mission/ad")
	public String completeAdMission(HttpSession session) {

		User loginUser = (User) session.getAttribute("loginUser");

		if (loginUser == null) {
			return "redirect:/login";
		}

		addMissionPoint(loginUser.getId(), 10, "広告視聴");

		User updatedUser = userMapper.findById(loginUser.getId());
		session.setAttribute("loginUser", updatedUser);

		return "redirect:/points";
	}

	/**
	 * Xで共有するミッション
	 */
	@PostMapping("/points/mission/share")
	public String completeShareMission(HttpSession session) {

		User loginUser = (User) session.getAttribute("loginUser");

		if (loginUser == null) {
			return "redirect:/login";
		}

		addMissionPoint(loginUser.getId(), 20, "X共有");

		User updatedUser = userMapper.findById(loginUser.getId());
		session.setAttribute("loginUser", updatedUser);

		return "redirect:/points";
	}

	/**
	 * ポイント履歴全件ページの表示
	 */
	@GetMapping("/points/history")
	public String showPointHistoryPage(HttpSession session, Model model) {

		User loginUser = (User) session.getAttribute("loginUser");

		if (loginUser == null) {
			return "redirect:/login";
		}

		User currentUser = userMapper.findById(loginUser.getId());

		List<PointHistory> allHistory = pointHistoryMapper.findByUserEmail(loginUser.getEmail());

		model.addAttribute("loginUser", currentUser);
		model.addAttribute("user", currentUser);
		model.addAttribute("currentPoint", currentUser.getPoint());
		model.addAttribute("pointHistoryList", allHistory);

		return "point-history";
	}

	/**
	 * ミッション達成時のポイント付与共通処理
	 */
	private void addMissionPoint(Integer userId, Integer missionPoint, String reason) {

		User currentUser = userMapper.findById(userId);

		Integer currentPoint = currentUser.getPoint();
		Integer newPoint = currentPoint + missionPoint;

		userMapper.updatePoint(userId, newPoint);

		PointHistory pointHistory = new PointHistory();
		pointHistory.setUserId(userId);
		pointHistory.setPointChange(missionPoint);
		pointHistory.setReason(reason);
		pointHistory.setCreatedAt(LocalDateTime.now());

		pointHistoryMapper.insert(pointHistory);
	}
}