CREATE TABLE IF NOT EXISTS skus (
    id VARCHAR(50) NOT NULL,
    product_id VARCHAR(50) NOT NULL,     -- @JoinColumn(name = "product_id") による外部キーカラム
    color VARCHAR(100) NULL,              -- SkuJpaEntityのフィールド
    size VARCHAR(50) NULL,                -- SkuJpaEntityのフィールド
    stock_quantity INT NOT NULL DEFAULT 0, -- キャメルケースから自動変換（stockQuantity -> stock_quantity）
    PRIMARY KEY (id),
    -- 親テーブルのデータが削除されたら、連動してこのSKUも削除する設定（ON DELETE CASCADE）
    CONSTRAINT fk_skus_product_id FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
    )