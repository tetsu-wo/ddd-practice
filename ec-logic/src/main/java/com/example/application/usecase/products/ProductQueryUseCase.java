package com.example.application.usecase.products;

import com.example.domain.model.products_inventory.Product;
import com.example.domain.model.products_inventory.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.presentation.dto.ProductDetailResponse;

/**
 * 商品参照ユースケース (アプリケーションサービス)
 */
@Service
@RequiredArgsConstructor
public class ProductQueryUseCase {

    private final ProductRepository productRepository;

    /**
     * 商品の詳細情報を取得して画面用の形にする手順
     */
    @Transactional(readOnly = true) // ⭕ 読み取り専用トランザクション
    public ProductDetailResponse getProductDetail(String productId) {
        // ① リポジトリから、集約ルート（Product）を取得する
        Product product = productRepository.findById(productId);
        if (product == null) {
            throw new IllegalArgumentException("指定された商品が見つかりません。");
        }

        // ② あなたの導き出した大原則通り、productから直接画面用のデータを組み立てて返す
        return new ProductDetailResponse(
                product.getProductIdentity().id(),
                product.getProductIdentity().name(),
                product.getStatus(),
                product.toSkuResponses() // SKUは露出せずProduct内でSkuResponseへ型ハメされる
        );
    }

}

