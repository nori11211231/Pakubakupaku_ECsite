package com.example.hokkaidoec.service;

import java.lang.reflect.Method;

import org.springframework.stereotype.Service;

import com.example.hokkaidoec.entity.AiGrowth;
import com.example.hokkaidoec.entity.Category;
import com.example.hokkaidoec.entity.Product;
import com.example.hokkaidoec.entity.Region;

@Service
public class AiService {

	public AiGrowth createDefaultAiGrowth() {
		AiGrowth aiGrowth = new AiGrowth();
		aiGrowth.setName("コンシェルジュ");
		aiGrowth.setGrowthStage(1);
		aiGrowth.setPersonality("やさしい");
		aiGrowth.setCharaImageUrl("/images/ai/chara_stage3.webp");
		return aiGrowth;
	}

	public String resolveCharaImageUrl(AiGrowth aiGrowth) {
		if (aiGrowth == null) {
			return "/images/ai/chara_stage1.webp";
		}

		Integer growthStage = aiGrowth.getGrowthStage();

		if (growthStage == null || growthStage == 1) {
			return "/images/ai/chara_stage1.webp";
		}

		if (growthStage == 2) {
			return "/images/ai/chara_stage2.webp";
		}

		if (aiGrowth.getCharaImageUrl() == null || aiGrowth.getCharaImageUrl().isBlank()) {
			return "/images/ai/chara_stage3.webp";
		}

		return aiGrowth.getCharaImageUrl();
	}

	public String createPageMessage(String currentPath, AiGrowth aiGrowth) {
		if (aiGrowth == null) {
			aiGrowth = createDefaultAiGrowth();
		}

		String message;

		if (currentPath == null || currentPath.equals("/") || currentPath.equals("/top")) {
			message = "北海道のおいしい商品を一緒に探しましょう";
		} else if (currentPath.startsWith("/products")) {
			message = "気になる商品を見つけたら、商品詳細を確認できます";
		} else if (currentPath.startsWith("/regions")) {
			message = "地域から北海道の名産品を探せます";
		} else if (currentPath.startsWith("/order/confirm")) {
			message = "注文内容、ポイント、支払金額を確認しましょう";
		} else if (currentPath.startsWith("/order/complete")) {
			message = "注文完了です。到着を楽しみにしてください";
		} else if (currentPath.startsWith("/orders")) {
			message = "過去の注文内容を確認できます";
		} else if (currentPath.startsWith("/point")) {
			message = "ポイント数やポイント履歴を確認できます";
		} else if (currentPath.startsWith("/game")) {
			message = "ポイントを使ってミニゲームに挑戦できます";
		} else if (currentPath.startsWith("/mypage")) {
			message = "登録情報や注文履歴を確認できます";
		} else if (currentPath.startsWith("/ai")) {
			message = "商品探しや操作方法について質問できます";
		} else if (currentPath.startsWith("/login")) {
			message = "ログインするとポイントや注文履歴を確認できます";
		} else if (currentPath.startsWith("/register")) {
			message = "会員登録すると北海道物産ECサイトをもっと便利に使えます";
		} else {
			message = "北海道物産ECサイトの使い方を案内します";
		}

		message = adjustByGrowthStage(message, aiGrowth);
		message = applyPersonality(message, aiGrowth);

		return message;
	}

	public String createProductMessage(Product product, Category category, Region region, AiGrowth aiGrowth) {
		if (aiGrowth == null) {
			aiGrowth = createDefaultAiGrowth();
		}

		String productName = getText(product, "getProductName", "getName", "getProduct_name");
		String description = getText(product, "getDescription", "getProductDescription");
		String price = getText(product, "getPrice");
		String stock = getText(product, "getStock");
		String categoryName = getText(category, "getCategoryName", "getName", "getCategory_name");
		String regionName = getText(region, "getRegionName", "getName", "getRegion_name");

		if (productName.isBlank()) {
			productName = "この商品";
		}

		Integer growthStage = aiGrowth.getGrowthStage();

		String message;

		if (growthStage == null || growthStage == 1) {
			message = productName + "、おすすめ";
		} else if (growthStage == 2) {
			message = productName + "は、" + regionName + "のおすすめ商品です。価格は" + price + "円です";
		} else {
			message = productName + "は、" + regionName + "の" + categoryName + "カテゴリの商品です。"
					+ "価格は" + price + "円、在庫は" + stock + "個です。"
					+ "特徴は「" + description + "」です。北海道らしい魅力を楽しみたい方におすすめです";
		}

		message = applyPersonality(message, aiGrowth);

		return message;
	}

	public String createChatReply(String userMessage, AiGrowth aiGrowth) {
		if (aiGrowth == null) {
			aiGrowth = createDefaultAiGrowth();
		}

		if (userMessage == null || userMessage.isBlank()) {
			return applyPersonality("質問を入力すると、商品探しや操作方法を案内できます", aiGrowth);
		}

		String text = userMessage.trim();
		String reply;

		if (text.contains("おすすめ")) {
			reply = "おすすめ商品を探すなら、海産物、乳製品、スイーツのカテゴリを見てみるのがおすすめです";
		} else if (text.contains("送料")) {
			reply = "送料や支払金額は、注文確認画面で確認できます";
		} else if (text.contains("ポイント")) {
			reply = "ポイントはポイントページで確認できます。ポイント履歴やミッションも確認できます";
		} else if (text.contains("地域")) {
			reply = "地域から探す画面では、北海道の地域ごとに商品を探せます";
		} else if (text.contains("注文") || text.contains("購入")) {
			reply = "購入する場合は、商品一覧から商品詳細を開き、注文確認画面で内容を確認してください";
		} else if (text.contains("ゲーム") || text.contains("スロット")) {
			reply = "ミニゲームではポイントを使ってスロットに挑戦できます";
		} else if (text.contains("履歴")) {
			reply = "注文履歴やポイント履歴から、過去の利用内容を確認できます";
		} else {
			reply = "北海道物産ECサイトの商品探しや操作方法について案内できます";
		}

		reply = adjustByGrowthStage(reply, aiGrowth);
		reply = applyPersonality(reply, aiGrowth);

		return reply;
	}

	public Integer calculateGrowthStage(Integer totalPurchaseAmount) {
		if (totalPurchaseAmount == null) {
			return 1;
		}

		if (totalPurchaseAmount >= 100000) {
			return 4;
		}

		if (totalPurchaseAmount >= 50000) {
			return 3;
		}

		if (totalPurchaseAmount >= 20000) {
			return 2;
		}

		return 1;
	}

	private String adjustByGrowthStage(String message, AiGrowth aiGrowth) {
		Integer growthStage = aiGrowth.getGrowthStage();

		if (growthStage == null || growthStage == 1) {
			if (message.length() > 35) {
				return "ぼく、案内するね";
			}
			return message;
		}

		if (growthStage == 2) {
			return message;
		}

		return message + "。詳しく知りたいときはAI相談画面で質問してください";
	}

	private String applyPersonality(String message, AiGrowth aiGrowth) {
		if (message == null || message.isBlank()) {
			return "";
		}

		String personality = aiGrowth.getPersonality();

		if (personality == null || personality.isBlank()) {
			return message + "。";
		}

		switch (personality) {
		case "元気":
			return message + "だよ！";
		case "明るい":
			return message + "だね！";
		case "やさしい":
			return message + "ですよ";
		case "知的":
			return message + "と考えられます。";
		case "上品":
			return message + "でございます";
		case "のんびり":
			return message + "だよ〜";
		case "クール":
			return message + "だ。";
		case "頼れる":
			return message + "。任せてください";
		default:
			return message + "。";
		}
	}

	private String getText(Object target, String... methodNames) {
		if (target == null) {
			return "";
		}

		for (String methodName : methodNames) {
			try {
				Method method = target.getClass().getMethod(methodName);
				Object value = method.invoke(target);

				if (value != null) {
					return String.valueOf(value);
				}
			} catch (Exception e) {
				// getterが存在しない場合は次の候補を試す
			}
		}

		return "";
	}
}