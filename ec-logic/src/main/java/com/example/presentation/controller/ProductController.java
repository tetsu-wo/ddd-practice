package com.example.presentation.controller;

import com.example.application.usecase.products.ProductOrderUseCase;
import com.example.application.usecase.products.ProductQueryUseCase; // ⭕ 参照系ユースケースをインポート
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.presentation.dto.ProductDetailResponse;
import com.example.presentation.dto.ProductOrderRequest;

/**
 * 商品在庫アプリケーションのコントローラー (プレゼンテーション層)
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    // ⭕ 役割に応じて、2つの異なるアプリケーション（ユースケース）を使い分ける
    private final ProductQueryUseCase productQueryUseCase;
    private final ProductOrderUseCase productOrderUseCase;

    /**
     * 1. 商品の詳細情報を取得する (参照系)
     * GET /api/products/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailResponse> getProductDetail(@PathVariable String id) {
        // ⭕ コントローラーは何もせず、参照系ユースケースに丸投げするだけ
        ProductDetailResponse response = productQueryUseCase.getProductDetail(id);
        return ResponseEntity.ok(response);
    }

    /**
     * 2. 商品を注文する (更新系)
     * POST /api/products/{id}/order
     */
    @PostMapping("/{id}/order")
    public ResponseEntity<Void> orderProduct(
            @PathVariable String id,
            @RequestBody @Valid ProductOrderRequest request
    ) {
        // ⭕ コントローラーは何もせず、更新系ユースケースに丸投げするだけ
        productOrderUseCase.execute(
                id,
                request.getColorCode(),
                request.getSizeCode(),
                request.getQuantity()
        );
        return ResponseEntity.ok().build();
    }
}
