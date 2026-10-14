package domain.cart_price.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Cart {
    private final String id;
    private final List<CartItem> items = new ArrayList<>();
    private LocalDateTime lastModifiedAt;
    private static final int EXPIRE_MINUTES = 30; // 30分で有効期限切れ

    public Cart(String id, LocalDateTime createdAt) {
        this.id = id;
        this.lastModifiedAt = createdAt;
    }

    public void addProduct(String skuId, Price price, int quantity, LocalDateTime now) {
        if (isExpired(now)) throw new IllegalStateException("カートの有効期限が切れています。");
        items.add(new CartItem(skuId, price, quantity));
        this.lastModifiedAt = now; // 操作があったので時間を更新
    }

    // 外部の現在時刻（now）を受け取って、自分が期限切れか判定する動的ロジック
    public boolean isExpired(LocalDateTime now) {
        return now.isAfter(lastModifiedAt.plusMinutes(EXPIRE_MINUTES));
    }

    // 「商品単価 × 数量」の総和（税込み）を計算する
    public int calculateTotalAmountWithTax() {
        return items.stream()
                .mapToInt(item -> item.price().totalWithTax() * item.quantity())
                .sum();
    }
}
// カートの中身（補助的な値オブジェクト）
record CartItem(String skuId, Price price, int quantity) {}

