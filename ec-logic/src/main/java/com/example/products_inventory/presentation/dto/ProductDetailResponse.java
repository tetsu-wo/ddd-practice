package com.example.products_inventory.presentation.dto;

import java.util.List;

/**
 * 商品詳細レスポンスDTO (画面へ返すJSONの構造)
 * ★Java標準のrecordを使用し、完全なイミュータブル性と安全性を両立します。
 */
public record ProductDetailResponse(
        String productId,
        String productName,
        String status,
        List<SkuResponse> skus
) {
    /**
     * 画面出力用の、SKUのデータ構造
     * ★ネストされたrecordは自動的に static 扱いになります。
     */
    public record SkuResponse(
            String color,
            String size,
            Integer stockQuantity
    ) {}
}
