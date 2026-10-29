package com.example.application.usecase.products;

import com.example.domain.model.products_inventory.Color;
import com.example.domain.model.products_inventory.Product;
import com.example.domain.model.products_inventory.ProductRepository;
import com.example.domain.model.products_inventory.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

/**
 * 商品注文ユースケース (アプリケーションサービス)
 */
@Service
@RequiredArgsConstructor
public class ProductOrderUseCase {

    private final ProductRepository productRepository;

    /**
     * 注文を実行する手順
     */
    @Transactional // ⭕ データベースのコミット・ロールバックの責任を持つ
    public void execute(String productId, String colorCode, String sizeCode, int quantity) {
        // ① リポジトリから対象のProductを取得
        Product product = productRepository.findById(productId);
        if (product == null) {
            throw new IllegalArgumentException("指定された商品が見つかりません。");
        }

        // ② 画面から届いた文字列を、ドメインの型（Record）にがちゃんこする
        Color color = new Color(colorCode);
        Size size = new Size(sizeCode);
        LocalDateTime orderDate = LocalDateTime.now();

        // ③ 集約ルートの窓口だけを叩き、ビジネスルールを実行
//        product.order(color, size, quantity, orderDate);

        // ④ 状態が更新された Product を保存（反映）
        productRepository.save(product);
    }
}

