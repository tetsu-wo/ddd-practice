package application;

import domain.products_inventory.*;
import java.time.LocalDateTime;

public class OrderStockApplicationService {
    private final domain.products_inventory.ProductRepository productRepository;

    // コンストラクタでインターフェース（約束）を受け取るだけ。中身は空っぽのままでOK
    public OrderStockApplicationService(domain.products_inventory.ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void executeOrder(String productId, String colorValue, String sizeValue, int quantity) {
        // 1. インターフェース（抽象）を叩いてデータを呼び出す
        domain.products_inventory.Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("商品が見つかりません。"));

        Color color = new Color(colorValue);
        domain.products_inventory.Size size = new domain.products_inventory.Size(sizeValue);
        LocalDateTime now = LocalDateTime.now();

        // 2. 集約を動かす
        product.order(color, size, quantity, now);

        // 3. インターフェース（抽象）を叩いて保存を依頼する
        productRepository.save(product);
    }
}
