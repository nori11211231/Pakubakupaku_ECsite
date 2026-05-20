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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.hokkaidoec.entity.Order;
import com.example.hokkaidoec.entity.OrderItem;
import com.example.hokkaidoec.entity.PointHistory;
import com.example.hokkaidoec.entity.Product;
import com.example.hokkaidoec.entity.User;
import com.example.hokkaidoec.form.OrderForm;
import com.example.hokkaidoec.mapper.AiGrowthMapper;
import com.example.hokkaidoec.mapper.OrderItemMapper;
import com.example.hokkaidoec.mapper.OrderMapper;
import com.example.hokkaidoec.mapper.PointHistoryMapper;
import com.example.hokkaidoec.mapper.ProductsMapper;
import com.example.hokkaidoec.mapper.UserMapper;

@Controller
public class OrderController {

	private final ProductsMapper productsMapper;
	private final OrderMapper orderMapper;
	private final OrderItemMapper orderItemMapper;
	private final UserMapper userMapper;
	private final PointHistoryMapper pointHistoryMapper;
	private final AiGrowthMapper aiGrowthMapper;

	public OrderController(
			ProductsMapper productsMapper,
			OrderMapper orderMapper,
			OrderItemMapper orderItemMapper,
			UserMapper userMapper,
			PointHistoryMapper pointHistoryMapper,
			AiGrowthMapper aiGrowthMapper) {

		this.productsMapper = productsMapper;
		this.orderMapper = orderMapper;
		this.orderItemMapper = orderItemMapper;
		this.userMapper = userMapper;
		this.pointHistoryMapper = pointHistoryMapper;
		this.aiGrowthMapper = aiGrowthMapper;
	}

	@GetMapping("/orders/{orderId}")
	public String orderDetail(@PathVariable("orderId") int orderId,
			HttpSession session,
			Model model) {

		User loginUser = (User) session.getAttribute("loginUser");
		if (loginUser == null) {
			return "redirect:/login";
		}

		// 注文情報
		Order order = orderMapper.findById(orderId);

		// 明細情報
		List<Map<String, Object>> orderItemDetails = orderItemMapper.findDetailsByOrderId(orderId);

		model.addAttribute("order", order);
		model.addAttribute("orderItemDetails", orderItemDetails);

		return "order-detail";
	}

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
	// ① カートに追加
	// ============================================================
	@PostMapping("/order/add")
	public String addToCart(
			@RequestParam("productId") int productId,
			@RequestParam("quantity") int quantity,
			HttpSession session) {

		Product product = productsMapper.findById(productId);

		List<Map<String, Object>> cartItems = (List<Map<String, Object>>) session.getAttribute("cartItems");

		if (cartItems == null) {
			cartItems = new ArrayList<>();
		}

		boolean merged = false;

		for (Map<String, Object> item : cartItems) {
			if ((int) item.get("productId") == productId) {

				// 既存商品の数量を加算
				int newQuantity = (int) item.get("quantity") + quantity;
				item.put("quantity", newQuantity);

				// 小計を再計算
				item.put("itemSubtotal", product.getPrice() * newQuantity);

				merged = true;
				break;
			}
		}

		// 新規商品なら追加
		if (!merged) {
			Map<String, Object> newItem = new HashMap<>();
			newItem.put("productId", product.getId());
			newItem.put("productName", product.getProductName());
			newItem.put("price", product.getPrice());
			newItem.put("quantity", quantity);
			newItem.put("itemSubtotal", product.getPrice() * quantity);
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

		List<Map<String, Object>> cartItems = (List<Map<String, Object>>) session.getAttribute("cartItems");

		if (cartItems == null || cartItems.isEmpty()) {
			model.addAttribute("message", "カートに商品がありません。");
			return "order-confirm";
		}

		int totalAmount = cartItems.stream()
				.mapToInt(item -> (int) item.get("itemSubtotal"))
				.sum();

		User loginUser = (User) session.getAttribute("loginUser");

		model.addAttribute("cartItems", cartItems);
		model.addAttribute("totalAmount", totalAmount);
		model.addAttribute("userPoint", loginUser.getPoint());
		model.addAttribute("orderForm", new OrderForm());

		return "order-confirm";
	}

	//カートを削除する機能	
	@PostMapping("/order/remove")
	public String removeFromCart(@RequestParam("productId") int productId, HttpSession session) {

		List<Map<String, Object>> cartItems = (List<Map<String, Object>>) session.getAttribute("cartItems");

		if (cartItems != null) {
			cartItems.removeIf(item -> (int) item.get("productId") == productId);
		}

		session.setAttribute("cartItems", cartItems);

		return "redirect:/order/confirm";
	}

	//	カート数量を変更する機能
	@PostMapping("/order/updateQuantity")
	public String updateQuantity(
			@RequestParam("productId") int productId,
			@RequestParam("quantity") int quantity,
			HttpSession session,
			Model model) {

		List<Map<String, Object>> cartItems = (List<Map<String, Object>>) session.getAttribute("cartItems");

		if (cartItems == null) {
			return "redirect:/order/confirm";
		}

		Product product = productsMapper.findById(productId);

		if (product.getStock() < quantity) {
			model.addAttribute("error", product.getProductName() + " の在庫が不足しています。数量を 1 に戻しました。");
			quantity = 1;
		}

		for (Map<String, Object> item : cartItems) {
			if ((int) item.get("productId") == productId) {
				item.put("quantity", quantity);
				item.put("itemSubtotal", quantity * (int) item.get("price"));
			}
		}

		session.setAttribute("cartItems", cartItems);

		// ★ここを修正
		return "redirect:/order/confirm";
	}

	// ============================================================
	// ③ 注文確定処理
	// ============================================================
	@PostMapping("/order/complete")
	public String completeOrder(OrderForm orderForm, HttpSession session, Model model) {

		List<Map<String, Object>> cartItems = (List<Map<String, Object>>) session.getAttribute("cartItems");
		User loginUser = (User) session.getAttribute("loginUser");
		// カートが空の場合
		if (cartItems == null || cartItems.isEmpty()) {
			model.addAttribute("error", "商品がありません。");
			model.addAttribute("loginUser", loginUser);
			model.addAttribute("cartItems", cartItems); // ★追加
			return "order-confirm";
		}

		int totalAmount = cartItems.stream()
				.mapToInt(item -> (int) item.get("itemSubtotal"))
				.sum();

		int usedPoint = orderForm.getUsedPoint() == null ? 0 : orderForm.getUsedPoint();

		// ポイント不足チェック
		if (usedPoint > loginUser.getPoint()) {
			model.addAttribute("error", "ポイントが不足しています。");
			model.addAttribute("loginUser", loginUser);
			model.addAttribute("cartItems", cartItems); // ★追加
			return "order-confirm";
		}

		// 在庫チェック
		for (Map<String, Object> item : cartItems) {
			int productId = (int) item.get("productId");
			int quantity = (int) item.get("quantity");

			Product product = productsMapper.findById(productId);

			if (product.getStock() < quantity) {
				model.addAttribute("error", product.getProductName() + " の在庫が不足しています。");
				model.addAttribute("loginUser", loginUser);
				model.addAttribute("cartItems", cartItems); // ★追加
				return "order-confirm";
			}

		}

		// ランク倍率は Rank エンティティを持っていないため固定 1% とする
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

		// 在庫減算処理
		for (Map<String, Object> item : cartItems) {
			int productId = (int) item.get("productId");
			int quantity = (int) item.get("quantity");

			productsMapper.updateStock(productId, quantity);
		}

		// ユーザー更新
		int newPoint = loginUser.getPoint() - usedPoint + earnedPoint;
		int newTotalAmount = loginUser.getTotalPurchaseAmount() + totalAmount;

		userMapper.updatePoint(loginUser.getId(), newPoint);

		// ポイント履歴（使用）
		if (usedPoint > 0) {
			PointHistory used = new PointHistory();
			used.setUserId(loginUser.getId());
			used.setPointChange(-usedPoint);
			used.setReason("ORDER_USED");
			pointHistoryMapper.insert(used);
		}

		// ポイント履歴（獲得）
		PointHistory earned = new PointHistory();
		earned.setUserId(loginUser.getId());
		earned.setPointChange(earnedPoint);
		earned.setReason("ORDER_EARNED");
		pointHistoryMapper.insert(earned);

		// AI成長更新（購入金額を渡す）
		aiGrowthMapper.updateGrowthStage(loginUser.getId(), newTotalAmount);

		// カート削除
		session.removeAttribute("cartItems");

		return "order-complete";
	}

}
