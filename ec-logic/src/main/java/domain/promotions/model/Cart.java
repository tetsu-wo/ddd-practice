package domain.promotions.model;

import java.math.BigDecimal;
import java.util.List;

public record Cart(List<CartItem> items, String userId) {
    public int getTotalQuantity() {
        return items.stream().mapToInt(CartItem::quantity).sum();
    }

    public BigDecimal getRawTotalAmount() {
        return items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

