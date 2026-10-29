package com.example.domain.model.products_inventory;

import com.example.infrastructure.db.jpa.entity.SkuJpaEntity;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Product {
    @Getter
    private final ProductIdentity productIdentity;
    final List<SKU> skus;
    @Getter
    private String status;


    public static Product reconstruct(ProductIdentity productIdentity, List<SKU> skus, String status){
        return new Product(productIdentity, skus, status);
    }


    public Product(ProductIdentity productIdentity, List<SKU> skus, String status) {
        // 1. null チェック
        Objects.requireNonNull(productIdentity, "商品識別子は必須です。");
        Objects.requireNonNull(status, "ステータスは必須です。");

        // 2. リストのバリデーション（既存のもの）
        if (skus == null || skus.isEmpty()) {
            throw new IllegalArgumentException("商品は最低1つのバリエーションが必要です。");
        }


        // 代入処理
        this.productIdentity = productIdentity;
        this.skus = List.copyOf(skus); // 不変コレクションにする素晴らしい実装です
        this.status = status;
    }

    public void order(Color color, Size size, int quantity, LocalDateTime orderDate){
        this.validateSalesPeriod(orderDate);
        if (!this.getStatus().equals("公開")){
            throw new IllegalStateException("この商品は現在購入できません（非公開または売り切れ）。");
        } else {
            SKU targetSku = this.findSku(color, size);
            targetSku.allocateStock(quantity);
        }
    }

    public void cancelOrder(Color color, Size size, int quantity){
        SKU targetSku = this.findSku(color, size);
        targetSku.releaseStock(quantity);
    }

    private SKU findSku(Color color, Size size){
        return (SKU)this.skus.stream().filter((sku) -> sku.getColor().equals(color) && sku.getSize().equals(size)).findFirst().orElseThrow(() -> new IllegalArgumentException("指定されたバリデーションは存在しません。"));
    }

    private void validateSalesPeriod(LocalDateTime orderData){
        if(this.productIdentity.salesStartPeriod() != null && this.productIdentity.salesEndPeriod() != null){
            if(orderData.isBefore(this.productIdentity.salesStartPeriod()) || orderData.isAfter(this.productIdentity.salesEndPeriod())){
                throw new IllegalStateException("販売期間外のため、購入できません。");
            }
        }
    }



    /**
     * 【重要】読み取り専用のListを返すGetter
     * これにより、マッパーやStreamでの参照は許可しつつ、
     * 外部からの getSkus().add(newSku) のような不正な変更をランタイムエラー（UnsupportedOperationException）で防ぎます。
     */
    public List<SKU> getSkus() {
        return Collections.unmodifiableList(this.skus);
    }

    // --- 以下、集約の整合性を保つためのビジネスロジック（振る舞い）の例 ---

//    /**
//     * SKUを追加する（ビジネスルールを適用する）
//     */
//    public void addSku(SKU newSku) {
//        // 例: 同一のSKUコードが既に存在しないかチェックするなどの「集約の整合性チェック」をここに書く
//        if (this.skus.contains(newSku)) {
//            throw new IllegalStateException("Duplicate SKU cannot be added");
//        }
//        this.skus.add(newSku);
//    }


    // マッパーからデータを渡すための簡単な器（レコードなど）を集約内に定義しておく
    public record SkuReconstructDto(String id, Color color, Size size, int stockQuantity) {
    }
}
