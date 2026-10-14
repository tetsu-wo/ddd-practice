package domain.promotions.model;

import java.math.BigDecimal;

public record CartItem(String productId, BigDecimal price, int quantity, String categoryId) {
    public BigDecimal getSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}

