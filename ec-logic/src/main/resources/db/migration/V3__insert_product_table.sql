-- 1. 親テーブル（products）へのインサート
INSERT INTO products (
    id,
    name,
    sales_start_period,
    sales_end_period,
    status,
    version
) VALUES (
    'prod-uuid-0001',            -- 商品の識別子 (UUID等を想定)
    'プレミアムTシャツ',
    '2026-10-23 11:00:00',       -- 現在日時
    '2026-11-23 11:00:00',       -- 販売終了日時
    'AVAILABLE',
    0                            -- ⚠️楽観的ロック用。必ず「0」を設定
);

-- 2. 子テーブル（skus）へのバルクインサート
-- 親（product_id = 'prod-uuid-0001'）に紐付く複数のSKUを1つのSQLでまとめて挿入します
INSERT INTO skus (
    id,
    color,
    size,
    stock_quantity,
    product_id                   -- @JoinColumn(name = "product_id") で指定された外部キー
) VALUES
    ('sku-uuid-0001', 'RED',  'M', 100, 'prod-uuid-0001'),
    ('sku-uuid-0002', 'RED',  'L', 50,  'prod-uuid-0001'),
    ('sku-uuid-0003', 'BLUE', 'M', 80,  'prod-uuid-0001');

