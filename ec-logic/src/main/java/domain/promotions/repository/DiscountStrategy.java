package domain.promotions.repository;

import domain.promotions.model.Cart;
import java.math.BigDecimal;

public interface DiscountStrategy {
    BigDecimal calculateDiscount(Cart cart);
}

