package com.example.hokkaidoec.service;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.example.hokkaidoec.entity.AiGrowth;
import com.example.hokkaidoec.entity.Category;
import com.example.hokkaidoec.entity.Product;
import com.example.hokkaidoec.entity.Region;
import com.example.hokkaidoec.mapper.AiGrowthMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AiService {

	private static final String OPENAI_RESPONSES_URL = "https://api.openai.com/v1/responses";

	private static final int GOLD_RANK_ID = 3;

	private static final List<AiCharacterOption> REGION_CHARACTERS = List.of(
			new AiCharacterOption("sorachi", "空知コンシェルジュ", "/img/ai/chara_sorachi.png"),
			new AiCharacterOption("ishikari", "石狩コンシェルジュ", "/img/ai/chara_ishikari.png"),
			new AiCharacterOption("shiribeshi", "後志コンシェルジュ", "/img/ai/chara_shiribeshi.png"),
			new AiCharacterOption("iburi", "胆振コンシェルジュ", "/img/ai/chara_tanshin.png"),
			new AiCharacterOption("hidaka", "日高コンシェルジュ", "/img/ai/chara_hidaka.png"),
			new AiCharacterOption("oshima", "渡島コンシェルジュ", "/img/ai/chara_oshima.png"),
			new AiCharacterOption("hiyama", "檜山コンシェルジュ", "/img/ai/chara_hiyama.png"),
			new AiCharacterOption("kamikawa", "上川コンシェルジュ", "/img/ai/chara_kawakami.png"),
			new AiCharacterOption("rumoi", "留萌コンシェルジュ", "/img/ai/chara_rumoi.png"),
			new AiCharacterOption("soya", "宗谷コンシェルジュ", "/img/ai/chara_soya.png"),
			new AiCharacterOption("okhotsk", "オホーツクコンシェルジュ", "/img/ai/chara_oho-tsuku.png"),
			new AiCharacterOption("tokachi", "十勝コンシェルジュ", "/img/ai/chara_tokachi.png"),
			new AiCharacterOption("kushiro", "釧路コンシェルジュ", "/img/ai/chara_kushiro.png"),
			new AiCharacterOption("nemuro", "根室コンシェルジュ", "/img/ai/chara_nemro.png"));

	private final AiGrowthMapper aiGrowthMapper;
	private final Environment environment;
	private final ObjectMapper objectMapper;
	private final HttpClient httpClient;

	public AiService(AiGrowthMapper aiGrowthMapper, Environment environment) {
		this.aiGrowthMapper = aiGrowthMapper;
		this.environment = environment;
		this.objectMapper = new ObjectMapper();
		this.httpClient = HttpClient.newBuilder()
				.connectTimeout(Duration.ofSeconds(10))
				.build();
	}

	/**
	 * ログインユーザーのAIキャラを取得する。
	 * なければランク1として自動作成する。
	 */
	public AiGrowth getOrCreateAiGrowth(Integer userId) {
		return getOrCreateAiGrowth(userId, 1);
	}

	public AiGrowth getOrCreateAiGrowthByRank(Integer userId, Integer rankId) {
		if (userId == null) {
			return createDefaultAiGrowth();
		}

		Integer growthStage = normalizeGrowthStage(rankId);

		AiGrowth aiGrowth = aiGrowthMapper.findByUserId(userId);

		if (aiGrowth == null) {
			AiGrowth newAiGrowth = new AiGrowth();
			newAiGrowth.setUserId(userId);
			newAiGrowth.setGrowthStage(growthStage);
			newAiGrowth.setPersonality(getRandomPersonality());
			newAiGrowth.setUpdatedAt(LocalDateTime.now());

			if (isGoldOrHigher(growthStage)) {
				AiCharacterOption randomCharacter = getRandomRegionCharacter();
				newAiGrowth.setName(randomCharacter.getName());
				newAiGrowth.setCharaImageUrl(randomCharacter.getImageUrl());
			} else {
				newAiGrowth.setName(createAiNameByStage(growthStage));
				newAiGrowth.setCharaImageUrl(createImageUrlByStage(growthStage));
			}

			aiGrowthMapper.insert(newAiGrowth);

			AiGrowth createdAiGrowth = aiGrowthMapper.findByUserId(userId);

			if (createdAiGrowth == null) {
				return newAiGrowth;
			}

			return createdAiGrowth;
		}

		Integer currentStage = normalizeGrowthStage(aiGrowth.getGrowthStage());

		if (isGoldOrHigher(growthStage)) {
			return syncGoldOrHigherCharacter(userId, aiGrowth, currentStage, growthStage);
		}

		String correctImageUrl = createImageUrlByStage(growthStage);
		String correctName = createAiNameByStage(growthStage);

		if (!currentStage.equals(growthStage)
				|| aiGrowth.getCharaImageUrl() == null
				|| !aiGrowth.getCharaImageUrl().equals(correctImageUrl)) {

			aiGrowthMapper.updateGrowthStage(
					userId,
					growthStage,
					correctName,
					correctImageUrl);

			aiGrowth.setGrowthStage(growthStage);
			aiGrowth.setName(correctName);
			aiGrowth.setCharaImageUrl(correctImageUrl);
			aiGrowth.setUpdatedAt(LocalDateTime.now());
		}

		return aiGrowth;
	}

	/**
	 * ユーザーに対応するAI成長データを取得する。
	 * なければランクに応じたAIキャラを自動作成する。
	 */
	public AiGrowth getOrCreateAiGrowth(Integer userId, Integer rankId) {
		if (userId == null) {
			return createDefaultAiGrowth();
		}

		AiGrowth aiGrowth = aiGrowthMapper.findByUserId(userId);

		if (aiGrowth != null) {
			return aiGrowth;
		}

		Integer growthStage = normalizeGrowthStage(rankId);

		AiGrowth newAiGrowth = new AiGrowth();
		newAiGrowth.setUserId(userId);
		newAiGrowth.setName(createAiNameByStage(growthStage));
		newAiGrowth.setGrowthStage(growthStage);
		newAiGrowth.setPersonality(getRandomPersonality());
		newAiGrowth.setUpdatedAt(LocalDateTime.now());
		newAiGrowth.setCharaImageUrl(createImageUrlByStage(growthStage));

		aiGrowthMapper.insert(newAiGrowth);

		AiGrowth createdAiGrowth = aiGrowthMapper.findByUserId(userId);

		if (createdAiGrowth == null) {
			return newAiGrowth;
		}

		return createdAiGrowth;
	}

	/**
	 * 未ログイン時や一時表示用のAI。
	 * DBには保存しない。
	 */
	public AiGrowth createDefaultAiGrowth() {
		AiGrowth aiGrowth = new AiGrowth();
		aiGrowth.setName("たまご");
		aiGrowth.setGrowthStage(1);
		aiGrowth.setPersonality("やさしい");
		aiGrowth.setCharaImageUrl("/img/ai/chara_stage1.png");
		return aiGrowth;
	}

	private String getRandomPersonality() {
		List<String> personalities = List.of(
				"元気",
				"明るい",
				"やさしい",
				"知的",
				"上品",
				"のんびり",
				"クール",
				"頼れる");

		int index = ThreadLocalRandom.current().nextInt(personalities.size());
		return personalities.get(index);
	}

	public String resolveCharaImageUrl(AiGrowth aiGrowth) {
		if (aiGrowth == null) {
			return "/img/ai/chara_stage1.png";
		}

		Integer growthStage = normalizeGrowthStage(aiGrowth.getGrowthStage());

		if (growthStage == 1) {
			return "/img/ai/chara_stage1.png";
		}

		if (growthStage == 2) {
			return "/img/ai/chara_stage2.png";
		}

		if (aiGrowth.getCharaImageUrl() == null || aiGrowth.getCharaImageUrl().isBlank()) {
			return createImageUrlByStage(growthStage);
		}

		return aiGrowth.getCharaImageUrl();
	}

	public List<AiCharacterOption> getRegionCharacterOptions() {
		return REGION_CHARACTERS;
	}

	public boolean canChangeRegionCharacter(Integer rankId) {
		if (rankId == null) {
			return false;
		}

		return normalizeGrowthStage(rankId) >= GOLD_RANK_ID;
	}

	public AiGrowth changeRegionCharacter(Integer userId, Integer rankId, String charaKey) {
		if (userId == null) {
			return createDefaultAiGrowth();
		}

		Integer growthStage = normalizeGrowthStage(rankId);

		if (!canChangeRegionCharacter(growthStage)) {
			return getOrCreateAiGrowthByRank(userId, growthStage);
		}

		AiCharacterOption selectedCharacter = findRegionCharacterByKey(charaKey);

		if (selectedCharacter == null) {
			return getOrCreateAiGrowthByRank(userId, growthStage);
		}

		AiGrowth aiGrowth = getOrCreateAiGrowthByRank(userId, growthStage);

		aiGrowthMapper.updateGrowthStage(
				userId,
				growthStage,
				selectedCharacter.getName(),
				selectedCharacter.getImageUrl());

		aiGrowth.setGrowthStage(growthStage);
		aiGrowth.setName(selectedCharacter.getName());
		aiGrowth.setCharaImageUrl(selectedCharacter.getImageUrl());
		aiGrowth.setUpdatedAt(LocalDateTime.now());

		return aiGrowth;
	}

	public String getCurrentRegionCharacterKey(AiGrowth aiGrowth) {
		if (aiGrowth == null || aiGrowth.getCharaImageUrl() == null) {
			return "";
		}

		AiCharacterOption character = findRegionCharacterByImageUrl(aiGrowth.getCharaImageUrl());

		if (character == null) {
			return "";
		}

		return character.getKey();
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

		if (categoryName.isBlank()) {
			categoryName = "北海道物産";
		}

		if (regionName.isBlank()) {
			regionName = "北海道";
		}

		if (price.isBlank()) {
			price = "未設定";
		}

		if (stock.isBlank()) {
			stock = "未設定";
		}

		if (description.isBlank()) {
			description = "北海道らしい魅力のある商品";
		}

		Integer growthStage = normalizeGrowthStage(aiGrowth.getGrowthStage());

		String message;

		if (growthStage == 1) {
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

	/**
	 * 既存Controller用。
	 * aiService.createChatReply(userMessage, aiGrowth) の形でも動くように残す。
	 */
	public String createChatReply(String userMessage, AiGrowth aiGrowth) {
		return createChatReply(aiGrowth, userMessage, "AIチャット画面");
	}

	/**
	 * API対応版のAI返答作成。
	 * ai.mode=openai または openai.api.enabled=true のときだけOpenAI APIを使う。
	 */
	public String createChatReply(AiGrowth aiGrowth, String userMessage, String pageContext) {
		if (aiGrowth == null) {
			aiGrowth = createDefaultAiGrowth();
		}

		if (userMessage == null || userMessage.isBlank()) {
			return applyPersonality("質問を入力すると、商品探しや操作方法を案内できます", aiGrowth);
		}

		String trimmedMessage = userMessage.trim();

		if (!isOpenAiEnabled()) {
			return createFallbackReply(aiGrowth, trimmedMessage, pageContext)
					+ "\n\n※現在は ai.mode または openai.api.enabled が無効なので、開発用の定型文を返しています。";
		}

		if (!hasOpenAiApiKey()) {
			return createFallbackReply(aiGrowth, trimmedMessage, pageContext)
					+ "\n\n※OPENAI_API_KEY が未設定なので、開発用の定型文を返しています。";
		}

		try {
			String reply = createReplyByOpenAi(aiGrowth, trimmedMessage, pageContext);

			if (reply != null && !reply.isBlank()) {
				return reply.trim();
			}

			return "すみません、AIの返答を作れませんでした。もう一度質問してください。";

		} catch (Exception e) {
			System.err.println("[OpenAI API Error] " + e.getMessage());

			return createFallbackReply(aiGrowth, trimmedMessage, pageContext)
					+ "\n\n※OpenAI API通信に失敗したため、開発用の定型文に切り替えました。"
					+ "\n原因: " + e.getMessage();
		}
	}

	/**
	 * OpenAI Responses APIで返答を生成する。
	 * response.outputText() は使わず、JSONから直接返答テキストを取り出す。
	 */
	private String createReplyByOpenAi(AiGrowth aiGrowth, String userMessage, String pageContext) throws Exception {
		String apiKey = getOpenAiApiKey();
		String model = getOpenAiModel();
		String systemPrompt = createSystemPrompt(aiGrowth, pageContext);

		Map<String, Object> requestBodyMap = new LinkedHashMap<>();
		requestBodyMap.put("model", model);
		requestBodyMap.put("instructions", systemPrompt);
		requestBodyMap.put("input", userMessage);
		requestBodyMap.put("max_output_tokens", 300);
		requestBodyMap.put("store", false);

		String requestBody = objectMapper.writeValueAsString(requestBodyMap);

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(OPENAI_RESPONSES_URL))
				.timeout(Duration.ofSeconds(30))
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + apiKey)
				.POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
				.build();

		HttpResponse<String> response = httpClient.send(
				request,
				HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

		if (response.statusCode() < 200 || response.statusCode() >= 300) {
			throw new IllegalStateException("status=" + response.statusCode()
					+ ", body=" + shorten(response.body()));
		}

		String reply = extractOutputText(response.body());

		if (reply == null || reply.isBlank()) {
			throw new IllegalStateException("OpenAI APIの返答テキストを取得できませんでした。body=" + shorten(response.body()));
		}

		return reply;
	}

	/**
	 * OpenAIのJSONレスポンスから返答テキストを取り出す。
	 */
	private String extractOutputText(String responseBody) throws Exception {
		if (responseBody == null || responseBody.isBlank()) {
			return "";
		}

		JsonNode root = objectMapper.readTree(responseBody);

		JsonNode errorNode = root.path("error");

		if (errorNode.isObject()) {
			String errorMessage = errorNode.path("message").asText("");

			if (!errorMessage.isBlank()) {
				throw new IllegalStateException(errorMessage);
			}

			throw new IllegalStateException("OpenAI APIでエラーが発生しました。");
		}

		String directOutputText = root.path("output_text").asText("");

		if (!directOutputText.isBlank()) {
			return directOutputText;
		}

		StringBuilder builder = new StringBuilder();

		JsonNode outputArray = root.path("output");

		if (outputArray.isArray()) {
			for (JsonNode outputItem : outputArray) {
				JsonNode contentArray = outputItem.path("content");

				if (!contentArray.isArray()) {
					continue;
				}

				for (JsonNode contentItem : contentArray) {
					String type = contentItem.path("type").asText("");

					if ("output_text".equals(type)) {
						appendText(builder, contentItem.path("text").asText(""));
					} else if ("refusal".equals(type)) {
						appendText(builder, contentItem.path("refusal").asText(""));
					} else {
						appendText(builder, contentItem.path("text").asText(""));
					}
				}
			}
		}

		return builder.toString().trim();
	}

	private void appendText(StringBuilder builder, String text) {
		if (text == null || text.isBlank()) {
			return;
		}

		if (builder.length() > 0) {
			builder.append("\n");
		}

		builder.append(text);
	}

	private String createSystemPrompt(AiGrowth aiGrowth, String pageContext) {
		String aiName = aiGrowth != null && aiGrowth.getName() != null
				? aiGrowth.getName()
				: "コンシェルジュ";

		Integer growthStage = aiGrowth != null
				? normalizeGrowthStage(aiGrowth.getGrowthStage())
				: 1;

		String personality = aiGrowth != null && aiGrowth.getPersonality() != null
				? aiGrowth.getPersonality()
				: "やさしい";

		String stageText = switch (growthStage) {
		case 1 -> "まだ幼いAIキャラ。短く、少し不慣れな感じで話す。";
		case 2 -> "子どもっぽいAIキャラ。元気で親しみやすく話す。";
		case 3 -> "成長したAIキャラ。わかりやすく丁寧に案内する。";
		case 4 -> "かなり成長したAIキャラ。落ち着いて詳しく案内する。";
		default -> "北海道物産ECサイトの案内役として自然に話す。";
		};

		String safePageContext = pageContext == null || pageContext.isBlank()
				? "通常のAIチャット画面"
				: pageContext;

		return """
				あなたは北海道物産ECサイトのAIコンシェルジュです。
				ユーザーの商品探し、サイト操作、注文確認、ポイント、マイページなどを案内してください。

				【AIキャラ情報】
				名前: %s
				成長段階: %s
				性格: %s
				話し方: %s

				【現在のページ情報】
				%s

				【重要ルール】
				・日本語で返答する
				・1回の返答は2〜4文程度にする
				・北海道物産ECサイトの案内として自然に答える
				・商品購入を無理に押し付けない
				・分からないことは断定せず、確認を促す
				・SQL、APIキー、内部実装、DB情報などの機密情報は答えない
				・注文登録、在庫更新、ポイント計算、ログイン処理は行わない
				・AIは商品案内と操作案内だけを担当する
				""".formatted(
				aiName,
				growthStage,
				personality,
				stageText,
				safePageContext);
	}

	/**
	 * APIを使えない場合の開発用返答。
	 */
	private String createFallbackReply(AiGrowth aiGrowth, String userMessage, String pageContext) {
		if (aiGrowth == null) {
			aiGrowth = createDefaultAiGrowth();
		}

		String message = userMessage == null ? "" : userMessage.toLowerCase();
		String reply;

		if (message.contains("おすすめ") || message.contains("オススメ")) {
			reply = "おすすめ商品を探しているんですね。北海道のお菓子、海産物、乳製品などから選ぶと探しやすいです";
		} else if (message.contains("送料")) {
			reply = "送料についてですね。注文確認画面で配送先や合計金額とあわせて確認できます";
		} else if (message.contains("ポイント")) {
			reply = "ポイントはマイページやポイントページから確認できます。購入やミッションで増える仕組みです";
		} else if (message.contains("注文") || message.contains("購入")) {
			reply = "注文についてですね。商品詳細から注文確認画面へ進み、内容を確認してから確定できます";
		} else if (message.contains("商品")) {
			reply = "商品について知りたいんですね。商品一覧や商品詳細ページから、価格や説明を確認できます";
		} else if (message.contains("地域")) {
			reply = "地域から探す画面では、北海道の地域ごとに商品を探せます";
		} else if (message.contains("ゲーム") || message.contains("スロット")) {
			reply = "ミニゲームではポイントを使ってスロットに挑戦できます";
		} else if (message.contains("履歴")) {
			reply = "注文履歴やポイント履歴から、過去の利用内容を確認できます";
		} else {
			reply = "北海道物産ECサイトについて案内します。商品、注文、ポイント、マイページなど、気になることを聞いてください";
		}

		reply = adjustByGrowthStage(reply, aiGrowth);
		reply = applyPersonality(reply, aiGrowth);

		return reply;
	}

	private boolean isOpenAiEnabled() {
		String aiMode = environment.getProperty("ai.mode", "mock");
		String enabledText = environment.getProperty("openai.api.enabled", "false");

		return "openai".equalsIgnoreCase(aiMode)
				|| Boolean.parseBoolean(enabledText);
	}

	private boolean hasOpenAiApiKey() {
		String apiKey = getOpenAiApiKey();
		return apiKey != null && !apiKey.isBlank();
	}

	private String getOpenAiApiKey() {
		String apiKey = environment.getProperty("openai.api-key");

		if (apiKey == null || apiKey.isBlank()) {
			apiKey = environment.getProperty("openai.api.key");
		}

		if (apiKey == null || apiKey.isBlank()) {
			apiKey = System.getenv("OPENAI_API_KEY");
		}

		return apiKey == null ? "" : apiKey.trim();
	}

	private String getOpenAiModel() {
		String model = environment.getProperty("openai.model", "gpt-4.1-mini");

		if (model == null || model.isBlank()) {
			return "gpt-4.1-mini";
		}

		return model.trim();
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

	private Integer normalizeGrowthStage(Integer growthStage) {
		if (growthStage == null) {
			return 1;
		}

		if (growthStage < 1) {
			return 1;
		}

		if (growthStage > 4) {
			return 4;
		}

		return growthStage;
	}

	private String createAiNameByStage(Integer growthStage) {
		Integer stage = normalizeGrowthStage(growthStage);

		return switch (stage) {
		case 1 -> "たまご";
		case 2 -> "こども";
		case 3 -> "コンシェルジュ";
		case 4 -> "ベテランコンシェルジュ";
		default -> "コンシェルジュ";
		};
	}

	private String createImageUrlByStage(Integer growthStage) {
		Integer stage = normalizeGrowthStage(growthStage);

		return switch (stage) {
		case 1 -> "/img/ai/chara_stage1.png";
		case 2 -> "/img/ai/chara_stage2.png";
		case 3 -> "/img/ai/chara_stage3.png";
		case 4 -> "/img/ai/chara_stage4.png";
		default -> "/img/ai/chara_stage1.png";
		};
	}

	private AiGrowth syncGoldOrHigherCharacter(
			Integer userId,
			AiGrowth aiGrowth,
			Integer currentStage,
			Integer newStage) {

		AiCharacterOption currentCharacter = findRegionCharacterByImageUrl(aiGrowth.getCharaImageUrl());

		if (currentStage < GOLD_RANK_ID || currentCharacter == null) {
			AiCharacterOption randomCharacter = getRandomRegionCharacter();

			aiGrowthMapper.updateGrowthStage(
					userId,
					newStage,
					randomCharacter.getName(),
					randomCharacter.getImageUrl());

			aiGrowth.setGrowthStage(newStage);
			aiGrowth.setName(randomCharacter.getName());
			aiGrowth.setCharaImageUrl(randomCharacter.getImageUrl());
			aiGrowth.setUpdatedAt(LocalDateTime.now());

			return aiGrowth;
		}

		if (!currentStage.equals(newStage)
				|| aiGrowth.getName() == null
				|| !aiGrowth.getName().equals(currentCharacter.getName())) {

			aiGrowthMapper.updateGrowthStage(
					userId,
					newStage,
					currentCharacter.getName(),
					currentCharacter.getImageUrl());

			aiGrowth.setGrowthStage(newStage);
			aiGrowth.setName(currentCharacter.getName());
			aiGrowth.setCharaImageUrl(currentCharacter.getImageUrl());
			aiGrowth.setUpdatedAt(LocalDateTime.now());
		}

		return aiGrowth;
	}

	private boolean isGoldOrHigher(Integer growthStage) {
		if (growthStage == null) {
			return false;
		}

		return growthStage >= GOLD_RANK_ID;
	}

	private AiCharacterOption getRandomRegionCharacter() {
		int index = ThreadLocalRandom.current().nextInt(REGION_CHARACTERS.size());
		return REGION_CHARACTERS.get(index);
	}

	private AiCharacterOption findRegionCharacterByKey(String key) {
		if (key == null || key.isBlank()) {
			return null;
		}

		for (AiCharacterOption character : REGION_CHARACTERS) {
			if (character.getKey().equals(key)) {
				return character;
			}
		}

		return null;
	}

	private AiCharacterOption findRegionCharacterByImageUrl(String imageUrl) {
		if (imageUrl == null || imageUrl.isBlank()) {
			return null;
		}

		for (AiCharacterOption character : REGION_CHARACTERS) {
			if (character.getImageUrl().equals(imageUrl)) {
				return character;
			}
		}

		return null;
	}

	private String adjustByGrowthStage(String message, AiGrowth aiGrowth) {
		if (message == null || message.isBlank()) {
			return "";
		}

		if (aiGrowth == null) {
			return message;
		}

		Integer growthStage = normalizeGrowthStage(aiGrowth.getGrowthStage());

		if (growthStage == 1) {
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

		if (aiGrowth == null) {
			return message + "。";
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

	private String shorten(String text) {
		if (text == null) {
			return "";
		}

		if (text.length() <= 500) {
			return text;
		}

		return text.substring(0, 500) + "...";
	}

	public static class AiCharacterOption {

		private final String key;
		private final String name;
		private final String imageUrl;

		public AiCharacterOption(String key, String name, String imageUrl) {
			this.key = key;
			this.name = name;
			this.imageUrl = imageUrl;
		}

		public String getKey() {
			return key;
		}

		public String getName() {
			return name;
		}

		public String getImageUrl() {
			return imageUrl;
		}
	}
}