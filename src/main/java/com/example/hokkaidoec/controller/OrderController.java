package com.example.hokkaidoec.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.hokkaidoec.entity.Order;
import com.example.hokkaidoec.entity.OrderItem;
import com.example.hokkaidoec.entity.PointHistory;
import com.example.hokkaidoec.entity.Product;
import com.example.hokkaidoec.entity.User;
import com.example.hokkaidoec.form.OrderForm;
import com.example.hokkaidoec.mapper.OrderItemMapper;
import com.example.hokkaidoec.mapper.OrderMapper;
import com.example.hokkaidoec.mapper.PointHistoryMapper;
import com.example.hokkaidoec.mapper.ProductsMapper;
import com.example.hokkaidoec.service.AiService;

@Controller
public class OrderController {

	private final ProductsMapper productsMapper;
	private final OrderMapper orderMapper;
	private final OrderItemMapper orderItemMapper;
	private final PointHistoryMapper pointHistoryMapper;
	private final AiService aiService;
	private final JdbcTemplate jdbcTemplate;

	public OrderController(
			ProductsMapper productsMapper,
			OrderMapper orderMapper,
			OrderItemMapper orderItemMapper,
			PointHistoryMapper pointHistoryMapper,
			AiService aiService,
			JdbcTemplate jdbcTemplate) {

		this.productsMapper = productsMapper;
		this.orderMapper = orderMapper;
		this.orderItemMapper = orderItemMapper;
		this.pointHistoryMapper = pointHistoryMapper;
		this.aiService = aiService;
		this.jdbcTemplate = jdbcTemplate;
	}

	// ============================================================
	// ① カートに追加
	// ============================================================
	@PostMapping("/order/add")
	public String addToCart(
			@RequestParam("productId") int productId,
			@RequestParam("quantity") int quantity,
			HttpSession session) {

		User loginUser = (User) session.getAttribute("loginUser");
		if (loginUser == null) {
			return "redirect:/login";
		}

		Product product = productsMapper.findById(productId);
		if (product == null) {
			return "redirect:/";
		}

		int safeQuantity = Math.max(quantity, 1);

		List<Map<String, Object>> cartItems = (List<Map<String, Object>>) session.getAttribute("cartItems");
		if (cartItems == null) {
			cartItems = new ArrayList<>();
		}

		boolean merged = false;

		for (Map<String, Object> item : cartItems) {
			if ((int) item.get("productId") == productId) {
				int newQuantity = (int) item.get("quantity") + safeQuantity;

				item.put("quantity", newQuantity);
				item.put("itemSubtotal", product.getPrice() * newQuantity);

				merged = true;
				break;
			}
		}

		if (!merged) {
			Map<String, Object> newItem = new HashMap<>();
			newItem.put("productId", product.getId());
			newItem.put("productName", product.getProductName());
			newItem.put("price", product.getPrice());
			newItem.put("quantity", safeQuantity);
			newItem.put("itemSubtotal", product.getPrice() * safeQuantity);
			newItem.put("imageUrl", product.getImageUrl());

			cartItems.add(newItem);
		}

		session.setAttribute("cartItems", cartItems);

		return "redirect:/order/confirm";
	}

	// ============================================================
	// ② 注文確認ページ
	// ============================================================
	@GetMapping("/order/confirm")
	public String showOrderConfirm(Model model, HttpSession session) {

		User loginUser = (User) session.getAttribute("loginUser");
		if (loginUser == null) {
			return "redirect:/login";
		}

		List<Map<String, Object>> cartItems = (List<Map<String, Object>>) session.getAttribute("cartItems");

		if (cartItems == null || cartItems.isEmpty()) {
			model.addAttribute("message", "カートに商品がありません。");
			model.addAttribute("cartItems", new ArrayList<>());
			model.addAttribute("totalAmount", 0);
			model.addAttribute("userPoint", loginUser.getPoint());
			model.addAttribute("orderForm", new OrderForm());
			return "order-confirm";
		}

		int totalAmount = cartItems.stream()
				.mapToInt(item -> (int) item.get("itemSubtotal"))
				.sum();

		model.addAttribute("cartItems", cartItems);
		model.addAttribute("totalAmount", totalAmount);
		model.addAttribute("userPoint", loginUser.getPoint());
		model.addAttribute("orderForm", new OrderForm());

		return "order-confirm";
	}

	// ============================================================
	// カートから削除
	// ============================================================
	@PostMapping("/order/remove")
	public String removeFromCart(
			@RequestParam("productId") int productId,
			HttpSession session) {

		User loginUser = (User) session.getAttribute("loginUser");
		if (loginUser == null) {
			return "redirect:/login";
		}

		List<Map<String, Object>> cartItems = (List<Map<String, Object>>) session.getAttribute("cartItems");

		if (cartItems != null) {
			cartItems.removeIf(item -> (int) item.get("productId") == productId);
		}

		session.setAttribute("cartItems", cartItems);

		return "redirect:/order/confirm";
	}

	// ============================================================
	// カート数量変更
	// ============================================================
	@PostMapping("/order/updateQuantity")
	public String updateQuantity(
			@RequestParam("productId") int productId,
			@RequestParam("quantity") int quantity,
			HttpSession session) {

		User loginUser = (User) session.getAttribute("loginUser");
		if (loginUser == null) {
			return "redirect:/login";
		}

		List<Map<String, Object>> cartItems = (List<Map<String, Object>>) session.getAttribute("cartItems");

		if (cartItems != null) {
			for (Map<String, Object> item : cartItems) {
				if ((int) item.get("productId") == productId) {
					int safeQuantity = Math.max(quantity, 1);
					int price = (int) item.get("price");

					item.put("quantity", safeQuantity);
					item.put("itemSubtotal", price * safeQuantity);
					break;
				}
			}
		}

		session.setAttribute("cartItems", cartItems);

		return "redirect:/order/confirm";
	}

	// ============================================================
	// ③ 注文確定処理
	// ============================================================
	@PostMapping("/order/complete")
	public String completeOrder(
			OrderForm orderForm,
			HttpSession session,
			Model model) {

		User loginUser = (User) session.getAttribute("loginUser");
		if (loginUser == null) {
			return "redirect:/login";
		}

		List<Map<String, Object>> cartItems = (List<Map<String, Object>>) session.getAttribute("cartItems");

		if (cartItems == null || cartItems.isEmpty()) {
			model.addAttribute("message", "カートが空です。");
			model.addAttribute("cartItems", new ArrayList<>());
			model.addAttribute("totalAmount", 0);
			model.addAttribute("userPoint", loginUser.getPoint());
			model.addAttribute("orderForm", new OrderForm());
			return "order-confirm";
		}

		int totalAmount = cartItems.stream()
				.mapToInt(item -> (int) item.get("itemSubtotal"))
				.sum();

		int usedPoint = orderForm.getUsedPoint() == null ? 0 : orderForm.getUsedPoint();

		if (usedPoint < 0) {
			model.addAttribute("error", "使用ポイントが不正です。");
			model.addAttribute("cartItems", cartItems);
			model.addAttribute("totalAmount", totalAmount);
			model.addAttribute("userPoint", loginUser.getPoint());
			model.addAttribute("orderForm", orderForm);
			return "order-confirm";
		}

		if (usedPoint > loginUser.getPoint()) {
			model.addAttribute("error", "ポイントが不足しています。");
			model.addAttribute("cartItems", cartItems);
			model.addAttribute("totalAmount", totalAmount);
			model.addAttribute("userPoint", loginUser.getPoint());
			model.addAttribute("orderForm", orderForm);
			return "order-confirm";
		}

		if (usedPoint > totalAmount) {
			model.addAttribute("error", "使用ポイントが注文金額を超えています。");
			model.addAttribute("cartItems", cartItems);
			model.addAttribute("totalAmount", totalAmount);
			model.addAttribute("userPoint", loginUser.getPoint());
			model.addAttribute("orderForm", orderForm);
			return "order-confirm";
		}

		// 在庫チェック
		for (Map<String, Object> item : cartItems) {
			int productId = (int) item.get("productId");
			int quantity = (int) item.get("quantity");

			Product product = productsMapper.findById(productId);

			if (product == null) {
				model.addAttribute("error", "商品情報が見つかりません。");
				model.addAttribute("cartItems", cartItems);
				model.addAttribute("totalAmount", totalAmount);
				model.addAttribute("userPoint", loginUser.getPoint());
				model.addAttribute("orderForm", orderForm);
				return "order-confirm";
			}

			if (product.getStock() < quantity) {
				model.addAttribute("error", product.getProductName() + " の在庫が不足しています。");
				model.addAttribute("cartItems", cartItems);
				model.addAttribute("totalAmount", totalAmount);
				model.addAttribute("userPoint", loginUser.getPoint());
				model.addAttribute("orderForm", orderForm);
				return "order-confirm";
			}
		}

		// 現状は固定1%
		int earnedPoint = (int) Math.floor(totalAmount * 0.01);

		// 注文登録
		Order order = new Order();
		order.setUserId(loginUser.getId());
		order.setShippingAddress(orderForm.getShippingAddress());
		order.setOrderDate(LocalDateTime.now());
		order.setTotalAmount(totalAmount - usedPoint);
		order.setUsedPoint(usedPoint);
		order.setEarnedPoint(earnedPoint);
		order.setOrderStatus("ORDERED");

		orderMapper.insert(order);

		// 注文明細登録
		for (Map<String, Object> item : cartItems) {
			OrderItem orderItem = new OrderItem();
			orderItem.setOrderId(order.getId());
			orderItem.setProductId((Integer) item.get("productId"));
			orderItem.setQuantity((Integer) item.get("quantity"));
			orderItem.setProductPrice((Integer) item.get("price"));

			orderItemMapper.insert(orderItem);
		}

		// 在庫減算
		for (Map<String, Object> item : cartItems) {
			int productId = (int) item.get("productId");
			int quantity = (int) item.get("quantity");

			productsMapper.updateStock(productId, quantity);
		}

		// ユーザーのポイント・累計購入金額・ランク更新
		int newPoint = loginUser.getPoint() - usedPoint + earnedPoint;
		int newTotalAmount = loginUser.getTotalPurchaseAmount() + totalAmount;
		int newRankId = findRankIdByTotalPurchaseAmount(newTotalAmount);

		updateUserPointTotalAmountAndRank(
				loginUser.getId(),
				newPoint,
				newTotalAmount,
				newRankId);

		// セッション上のログインユーザーも更新
		loginUser.setPoint(newPoint);
		loginUser.setTotalPurchaseAmount(newTotalAmount);
		loginUser.setRankId(newRankId);
		session.setAttribute("loginUser", loginUser);

		// AIキャラをランクに合わせて作成・更新
		// 1: ブロンズ = 卵
		// 2: シルバー = 子供
		// 3以上: ゴールド以上 = 14振興局キャラから選択可能
		// ゴールドに上がった瞬間は AiService 側でランダムな振興局キャラに変更される
		aiService.getOrCreateAiGrowthByRank(loginUser.getId(), newRankId);

		// ポイント履歴 使用分
		if (usedPoint > 0) {
			PointHistory used = new PointHistory();
			used.setUserId(loginUser.getId());
			used.setPointChange(-usedPoint);
			used.setReason("ORDER_USED");
			pointHistoryMapper.insert(used);
		}

		// ポイント履歴 獲得分
		PointHistory earned = new PointHistory();
		earned.setUserId(loginUser.getId());
		earned.setPointChange(earnedPoint);
		earned.setReason("ORDER_EARNED");
		pointHistoryMapper.insert(earned);

		// カート削除
		session.removeAttribute("cartItems");

		return "order-complete";
	}

	// ============================================================
	// 注文履歴
	// ============================================================
	@GetMapping("/order/history")
	public String orderHistory(HttpSession session, Model model) {

		User loginUser = (User) session.getAttribute("loginUser");
		if (loginUser == null) {
			return "redirect:/login";
		}

		int userId = loginUser.getId();
		List<Order> orderList = orderMapper.findByUserId(userId);

		model.addAttribute("orderList", orderList);

		return "order-history";
	}

	// ============================================================
	// 注文詳細
	// ============================================================
	@GetMapping("/orders/{orderId}")
	public String orderDetail(
			@PathVariable("orderId") int orderId,
			HttpSession session,
			Model model) {

		User loginUser = (User) session.getAttribute("loginUser");
		if (loginUser == null) {
			return "redirect:/login";
		}

		Order order = orderMapper.findById(orderId);
		List<Map<String, Object>> orderItemDetails = orderItemMapper.findDetailsByOrderId(orderId);

		model.addAttribute("order", order);
		model.addAttribute("orderItemDetails", orderItemDetails);

		return "order-detail";
	}

	// ============================================================
	// 累計購入金額からランクIDを取得
	// ranks.min_amount を基準に一番近いランクを取得する
	// ============================================================
	private int findRankIdByTotalPurchaseAmount(int totalPurchaseAmount) {
		List<Integer> rankIds = jdbcTemplate.queryForList(
				"""
						SELECT
						    id
						FROM
						    ranks
						WHERE
						    min_amount <= ?
						ORDER BY
						    min_amount DESC
						LIMIT 1
						""",
				Integer.class,
				totalPurchaseAmount);

		if (rankIds == null || rankIds.isEmpty()) {
			return 1;
		}

		Integer rankId = rankIds.get(0);

		if (rankId == null) {
			return 1;
		}

		if (rankId < 1) {
			return 1;
		}

		if (rankId > 4) {
			return 4;
		}

		return rankId;
	}

	// ============================================================
	// usersテーブル更新
	// point / total_purchase_amount / rank_id をまとめて更新
	// ============================================================
	private void updateUserPointTotalAmountAndRank(
			int userId,
			int point,
			int totalPurchaseAmount,
			int rankId) {

		jdbcTemplate.update(
				"""
						UPDATE
						    users
						SET
						    point = ?,
						    total_purchase_amount = ?,
						    rank_id = ?
						WHERE
						    id = ?
						""",
				point,
				totalPurchaseAmount,
				rankId,
				userId);
	}
}