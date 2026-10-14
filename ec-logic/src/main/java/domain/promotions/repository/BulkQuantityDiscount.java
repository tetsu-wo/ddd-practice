package domain.promotions.repository;

import domain.promotions.model.Cart;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class BulkQuantityDiscount implements DiscountStrategy {
    private final int minQuantity;
    private final BigDecimal discountRate; // 例: 0.10 (10%OFF)

    public BulkQuantityDiscount(int minQuantity, BigDecimal discountRate) {
        this.minQuantity = minQuantity;
        this.discountRate = discountRate;
    }

    @Override
    public BigDecimal calculateDiscount(Cart cart) {
        if (cart.getTotalQuantity() < minQuantity) {
            return BigDecimal.ZERO;
        }
        // カート総額 × 割引率（四捨五入して円単位にする端数処理）
        return cart.getRawTotalAmount()
                .multiply(discountRate)
                .setScale(0, RoundingMode.HALF_UP);
    }
}

