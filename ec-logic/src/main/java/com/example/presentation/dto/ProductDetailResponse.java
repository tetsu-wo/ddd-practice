package com.example.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

/**
 * 商品詳細レスポンスDTO (画面へ返すJSONの構造)
 * ★Lombokの@Getterや@AllArgsConstructorをフル活用してシンプルに保ちます。
 */
@Getter
@AllArgsConstructor
public class ProductDetailResponse {

    private final String productId;
    private final String productName;
    private final String status;
    private final List<SkuResponse> skus;

    /**
     * 画面出力用の、SKUのデータ構造
     * ★このクラス（型）を、ドメイン層の Product に「これに型ハメしてくれ」と頼みます。
     */
    @Getter
    @AllArgsConstructor
    public static class SkuResponse {
        private final String color;
        private final String size;
        private final int stockQuantity;
    }
}
