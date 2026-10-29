package com.example.domain.model.products_inventory;


import lombok.Getter;

public class SKU {

    @Getter
    private final SkuIdentity skuidentity;
    private int stockQuantity;

    public SKU(SkuIdentity skuidentity, int stockQuantity) {
        if (stockQuantity < 0) throw new IllegalArgumentException("在庫数は0以上である必要があります。");
        this.skuidentity = skuidentity;
        this.stockQuantity = stockQuantity;
    }

    Color getColor() { return this.skuidentity.color(); }
    Size getSize() { return this.skuidentity.size(); }
    int getStockQuantity() { return stockQuantity; }


    void allocateStock(int quantity) {
        if (this.stockQuantity < quantity) {
            throw new IllegalStateException("在庫不足です。現在の残り: " + this.stockQuantity);
        }
        this.stockQuantity -= quantity;
    }

    void releaseStock(int quantity) {
        this.stockQuantity += quantity;
    }
}