-- =========================
-- ranks
-- =========================
INSERT INTO ranks (id, rank_name, min_amount, point_rate) VALUES
(1, 'ブロンズ', 0, 0.01),
(2, 'シルバー', 20000, 0.02),
(3, 'ゴールド', 50000, 0.03),
(4, 'プラチナ', 100000, 0.05);

-- =========================
-- categories
-- =========================
INSERT INTO categories (id, category_name) VALUES
(1, '海産物'),
(2, '乳製品'),
(3, '農産物'),
(4, 'スイーツ'),
(5, '加工食品'),
(6, '飲料'),
(7, '雑貨');

-- =========================
-- regions
-- =========================
INSERT INTO regions (id, region_name) VALUES
(1, '空知'),
(2, '石狩'),
(3, '後志'),
(4, '胆振'),
(5, '日高'),
(6, '渡島'),
(7, '檜山'),
(8, '上川'),
(9, '留萌'),
(10, '宗谷'),
(11, 'オホーツク'),
(12, '十勝'),
(13, '釧路'),
(14, '根室');

-- =========================
-- products
-- =========================
INSERT INTO products
(id, product_name, description, price, stock, image_url, category_id, region_id)
VALUES
(1, '夕張メロンゼリー', '空知地域の夕張メロンを使った香り豊かなゼリーです。', 1200, 50, '/img/product/yuubarimeron.jpg', 4, 1),
(2, '空知産ゆめぴりか 5kg', '北海道米ゆめぴりかの5kgパックです。', 3200, 30, '/img/product/yumepirika.webp', 3, 1),

(3, '石狩鍋セット', '鮭と野菜の旨味を楽しめる北海道名物の鍋セットです。', 4500, 20, '/img/product/isikarinabe.jpg', 5, 2),
(4, '札幌スープカレー', 'スパイスの効いた札幌名物スープカレーです。', 980, 80, '/img/product/sapporokare-.png', 5, 2),

(5, '小樽チーズケーキ', '濃厚なチーズの風味が楽しめる小樽スイーツです。', 1800, 40, '/img/product/otaruce-ki.jpg', 4, 3),
(6, 'ニセコ高原牛乳', '後志地域の自然で育まれたまろやかな牛乳です。', 450, 100, '/img/product/milk.webp', 2, 3),

(7, '登別温泉まんじゅう', '胆振地域の温泉地をイメージした定番まんじゅうです。', 900, 60, '/img/product/noboribetsu_manju.jpg', 4, 4),
(8, '苫小牧ホッキカレー', 'ホッキ貝の旨味を生かしたご当地カレーです。', 750, 70, '/img/product/tomakomai_hokki_curry.jpg', 5, 4),

(9, '日高昆布', 'だしに最適な日高産昆布です。', 1300, 45, '/img/product/hidaka_konbu.jpg', 1, 5),
(10, '日高ヨーグルト', 'さっぱりとした味わいの北海道ヨーグルトです。', 380, 90, '/img/product/hidaka_yogurt.webp', 2, 5),

(11, '函館塩辛', '新鮮なイカを使った函館名物の塩辛です。', 1100, 55, '/images/products/hakodate_shiokara.webp', 1, 6),
(12, '函館ラーメン', 'あっさり塩味の函館ラーメンセットです。', 850, 75, '/images/products/hakodate_ramen.webp', 5, 6),

(13, '檜山産じゃがいも', 'ホクホク食感が特徴のじゃがいもです。', 1500, 35, '/images/products/hiyama_potato.webp', 3, 7),
(14, '江差追分羊羹', '檜山地域の伝統を感じる和スイーツです。', 800, 65, '/images/products/esashi_yokan.webp', 4, 7),

(15, '富良野ラベンダーサシェ', 'ラベンダーの香りを楽しめる雑貨です。', 700, 100, '/images/products/furano_lavender_sachet.webp', 7, 8),
(16, '大雪山天然水', '上川地域の自然を感じる天然水です。', 120, 200, '/images/products/daisetsuzan_water.webp', 6, 8),

(17, '留萌産甘えび', '甘みの強い留萌産の甘えびです。', 2400, 25, '/images/products/rumoi_amaebi.webp', 1, 9),
(18, '留萌にしん甘露煮', 'にしんを甘辛く煮付けた加工食品です。', 980, 50, '/images/products/rumoi_nishin.webp', 5, 9),

(19, '宗谷ほたて', '肉厚で甘みのある宗谷産ほたてです。', 3000, 30, '/images/products/soya_hotate.webp', 1, 10),
(20, '稚内ミルクアイス', '北海道ミルクを使った濃厚アイスです。', 1500, 40, '/images/products/wakkanai_milk_ice.webp', 4, 10),

(21, 'オホーツク流氷カレー', '青色が特徴的なオホーツク風カレーです。', 700, 70, '/images/products/okhotsk_ryuhyo_curry.webp', 5, 11),
(22, '北見たまねぎ', '甘みのある北見産たまねぎです。', 1200, 80, '/images/products/kitami_onion.webp', 3, 11),

(23, '十勝チーズ', '十勝産ミルクを使った濃厚チーズです。', 1600, 60, '/images/products/tokachi_cheese.webp', 2, 12),
(24, '十勝小豆どら焼き', '十勝小豆を使ったどら焼きです。', 1000, 90, '/images/products/tokachi_dorayaki.webp', 4, 12),

(25, '釧路さんま缶詰', '釧路のさんまを使った保存しやすい缶詰です。', 600, 100, '/images/products/kushiro_sanma_can.webp', 1, 13),
(26, '阿寒湖まりも羊羹', '阿寒湖のまりもをイメージした羊羹です。', 850, 50, '/images/products/akan_marimo_yokan.webp', 4, 13),

(27, '根室花咲ガニ', '根室名物の花咲ガニです。', 6000, 15, '/images/products/nemuro_hanasaki_crab.webp', 1, 14),
(28, '根室昆布醤油', '昆布の旨味を生かした根室の醤油です。', 650, 80, '/images/products/nemuro_konbu_soy.webp', 5, 14);

-- =========================
-- users
-- passwordは仮データなので平文。本番ではハッシュ化すること。
-- =========================
INSERT INTO users
(id, name, email, password, address, phone, point, total_purchase_amount, rank_id)
VALUES
(1, '山田 太郎', 'taro@example.com', 'password', '北海道札幌市中央区1-1-1', '090-1111-1111', 450, 6500, 1),
(2, '佐藤 花子', 'hanako@example.com', 'password', '北海道函館市五稜郭町2-2-2', '090-2222-2222', 1200, 22000, 2),
(3, '鈴木 一郎', 'ichiro@example.com', 'password', '北海道旭川市3条通3-3-3', '090-3333-3333', 2400, 52000, 3),
(4, '高橋 美咲', 'misaki@example.com', 'password', '北海道帯広市西1条4-4-4', '090-4444-4444', 6000, 125000, 4),
(5, '田中 健', 'ken@example.com', 'password', '北海道小樽市色内5-5-5', '090-5555-5555', 300, 8000, 1),
(6, '伊藤 さくら', 'sakura@example.com', 'password', '北海道釧路市末広町6-6-6', '090-6666-6666', 1800, 35000, 2),
(7, '渡辺 翔', 'sho@example.com', 'password', '北海道北見市中央町7-7-7', '090-7777-7777', 3200, 72000, 3),
(8, '中村 葵', 'aoi@example.com', 'password', '北海道根室市花咲町8-8-8', '090-8888-8888', 8500, 150000, 4);

-- =========================
-- orders
-- total_amount = 商品小計 - used_point
-- order_status: ORDERED = 注文受付, SHIPPED = 発送済み, CANCELED = キャンセル
-- =========================
INSERT INTO orders
(id, user_id, shipping_address, order_date, total_amount, used_point, earned_point, order_status)
VALUES
(1, 1, '北海道札幌市中央区1-1-1', '2026-05-01 10:15:00', 5500, 100, 55, 'SHIPPED'),
(2, 2, '北海道函館市五稜郭町2-2-2', '2026-05-02 11:30:00', 6460, 0, 129, 'SHIPPED'),
(3, 3, '北海道旭川市3条通3-3-3', '2026-05-03 14:20:00', 6700, 500, 201, 'SHIPPED'),
(4, 4, '北海道帯広市西1条4-4-4', '2026-05-04 16:45:00', 14200, 1000, 710, 'ORDERED'),
(5, 5, '北海道小樽市色内5-5-5', '2026-05-05 09:10:00', 3300, 0, 33, 'SHIPPED'),
(6, 6, '北海道釧路市末広町6-6-6', '2026-05-06 13:00:00', 4550, 200, 91, 'SHIPPED'),
(7, 7, '北海道北見市中央町7-7-7', '2026-05-07 18:25:00', 12000, 0, 360, 'ORDERED'),
(8, 8, '北海道根室市花咲町8-8-8', '2026-05-08 20:05:00', 10800, 1000, 540, 'SHIPPED'),
(9, 3, '北海道旭川市3条通3-3-3', '2026-05-09 12:40:00', 7740, 0, 232, 'SHIPPED'),
(10, 4, '北海道帯広市西1条4-4-4', '2026-05-10 15:55:00', 7040, 500, 352, 'ORDERED');

-- =========================
-- order_items
-- =========================
INSERT INTO order_items
(order_id, product_id, quantity, product_price)
VALUES
(1, 1, 2, 1200),
(1, 2, 1, 3200),

(2, 3, 1, 4500),
(2, 4, 2, 980),

(3, 5, 3, 1800),
(3, 6, 4, 450),

(4, 27, 2, 6000),
(4, 23, 2, 1600),

(5, 7, 2, 900),
(5, 8, 2, 750),

(6, 11, 2, 1100),
(6, 12, 3, 850),

(7, 19, 3, 3000),
(7, 20, 2, 1500),

(8, 21, 4, 700),
(8, 22, 5, 1200),
(8, 24, 3, 1000),

(9, 17, 2, 2400),
(9, 18, 3, 980),

(10, 15, 5, 700),
(10, 16, 12, 120),
(10, 28, 4, 650);

-- =========================
-- point_history
-- =========================
INSERT INTO point_history
(id, user_id, point_change, reason, created_at)
VALUES
(1, 1, 55, '商品購入ポイント付与', '2026-05-01 10:15:00'),
(2, 1, -100, '商品購入時にポイント利用', '2026-05-01 10:15:00'),
(3, 2, 129, '商品購入ポイント付与', '2026-05-02 11:30:00'),
(4, 3, 201, '商品購入ポイント付与', '2026-05-03 14:20:00'),
(5, 3, -500, '商品購入時にポイント利用', '2026-05-03 14:20:00'),
(6, 4, 710, '商品購入ポイント付与', '2026-05-04 16:45:00'),
(7, 4, -1000, '商品購入時にポイント利用', '2026-05-04 16:45:00'),
(8, 5, 33, '商品購入ポイント付与', '2026-05-05 09:10:00'),
(9, 6, 91, '商品購入ポイント付与', '2026-05-06 13:00:00'),
(10, 6, -200, '商品購入時にポイント利用', '2026-05-06 13:00:00'),
(11, 7, 360, '商品購入ポイント付与', '2026-05-07 18:25:00'),
(12, 8, 540, '商品購入ポイント付与', '2026-05-08 20:05:00'),
(13, 8, -1000, '商品購入時にポイント利用', '2026-05-08 20:05:00'),
(14, 3, 232, '商品購入ポイント付与', '2026-05-09 12:40:00'),
(15, 4, 352, '商品購入ポイント付与', '2026-05-10 15:55:00'),
(16, 4, -500, '商品購入時にポイント利用', '2026-05-10 15:55:00'),
(17, 1, 100, 'スロットゲーム報酬', '2026-05-11 19:00:00'),
(18, 8, 300, 'スロットゲーム報酬', '2026-05-12 21:00:00');

-- =========================
-- ai_growth
-- ai_name ではなく name に修正
-- =========================
INSERT INTO ai_growth
(id, user_id, name, growth_stage, personality, updated_at, chara_image_url)
VALUES
(1, 1, 'ポチ', 1, '元気', '2026-05-01 10:20:00', '/img/ai/chara_stage1.webp'),
(2, 2, 'タマ', 2, 'やさしい', '2026-05-02 11:35:00', '/img/ai/chara_stage2.webp'),
(3, 3, 'Temu', 3, '知的', '2026-05-09 12:45:00', '/img/ai/chara_stage3.webp'),
(4, 4, '佐々木', 4, '上品', '2026-05-10 16:00:00', '/img/ai/chara_stage4.webp'),
(5, 5, '田中どんぐり', 1, 'のんびり', '2026-05-05 09:15:00', '/img/ai/chara_stage1.webp'),
(6, 6, '佐藤ちゃん', 2, '明るい', '2026-05-06 13:05:00', '/img/ai/chara_stage2.webp'),
(7, 7, 'ちいかわ', 3, 'クール', '2026-05-07 18:30:00', '/img/ai/chara_stage3.webp'),
(8, 8, 'はちわれ', 4, '頼れる', '2026-05-12 21:05:00', '/img/ai/chara_stage4.webp');

-- =========================
-- game_play_history
-- result: true = 当たり, false = はずれ
-- play_id ではなく id に修正
-- =========================
INSERT INTO game_play_history
(id, user_id, bet_point, result, earned_point, played_at)
VALUES
(1, 1, 50, true, 100, '2026-05-11 19:00:00'),
(2, 1, 30, false, 0, '2026-05-11 19:10:00'),
(3, 2, 100, false, 0, '2026-05-11 20:00:00'),
(4, 3, 100, true, 250, '2026-05-11 20:30:00'),
(5, 4, 200, true, 500, '2026-05-11 21:00:00'),
(6, 5, 50, false, 0, '2026-05-12 18:30:00'),
(7, 6, 80, true, 160, '2026-05-12 19:20:00'),
(8, 7, 100, false, 0, '2026-05-12 20:10:00'),
(9, 8, 150, true, 300, '2026-05-12 21:00:00'),
(10, 8, 100, false, 0, '2026-05-12 21:15:00');

-- =========================
-- シーケンス調整
-- 明示IDでINSERTしているため、次回自動採番が重複しないようにする
-- =========================
SELECT setval(pg_get_serial_sequence('ranks', 'id'), (SELECT MAX(id) FROM ranks));
SELECT setval(pg_get_serial_sequence('categories', 'id'), (SELECT MAX(id) FROM categories));
SELECT setval(pg_get_serial_sequence('regions', 'id'), (SELECT MAX(id) FROM regions));
SELECT setval(pg_get_serial_sequence('products', 'id'), (SELECT MAX(id) FROM products));
SELECT setval(pg_get_serial_sequence('users', 'id'), (SELECT MAX(id) FROM users));
SELECT setval(pg_get_serial_sequence('orders', 'id'), (SELECT MAX(id) FROM orders));
SELECT setval(pg_get_serial_sequence('point_history', 'id'), (SELECT MAX(id) FROM point_history));
SELECT setval(pg_get_serial_sequence('ai_growth', 'id'), (SELECT MAX(id) FROM ai_growth));
SELECT setval(pg_get_serial_sequence('game_play_history', 'id'), (SELECT MAX(id) FROM game_play_history));